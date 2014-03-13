package dao.impl.orm.slick

import common._
import models._
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import models.CategoryEntity
import models.ProductDetails
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation


import play.api.i18n.Messages
import wt.common.image.ImageEntity

class ProductRepository extends RepositoryBase with dao.common.ProductRepository {
  def listMostVisited(count: Int) = database withDynSession {
    implicit val res = GetResult(r => new ProductDetails(r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        p.title,
        p.short_description,
        p.price,
        p.id,
        p.default_image_id
      from
        products p
      where
        p.archived = false and
        p.price > 0 and
        p.is_available = true
      limit ${count};
    """.as[ProductDetails].list
  }

  override def getAvailability(productId: Int): Int = database withDynSession {
    sql"""
      select sum(quantity) from stock_items where product_id = ${productId};
    """.as[Int].first
  }


  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int, search: String, inStock: Boolean): ListPage[ProductDetails] = database withDynSession {
    implicit val getProducts = GetResult(
      r => new ProductDetails(r.nextString, r.nextString, r.nextDouble, r.nextInt, r.nextInt, r.nextBoolean))
    val category = getCategory
    val products = sql"""
      select
        prod.title,
        prod.short_description,
        prod.price,
        prod.id,
        prod.defaultImageId,
        prod.is_available
      from
        products prod,
        products_categories prod_cat,
        categories cat
      where
        prod.archived = false and
        (prod.id = prod_cat.productId) and
        (prod_cat.categoryId = cat.category_id) and
        (cat.left_value >= ${category.leftValue}) and
        (cat.right_value <= ${category.rightValue}) and
        ((${brandId} = 0) or (prod.brand_id = ${brandId})) and
        ((${search} = '') or (prod.title like ${'%'+search+'%'})) and
        ((${inStock} = FALSE) or (prod.is_available = ${inStock}))
      limit ${(pageNumber - 1)*pageSize}, ${pageSize}
    """.as[ProductDetails].list
    val countQuery = sql"""
      select
        count(prod.title)
      from
        products prod,
        products_categories prod_cat,
        categories cat
      where
        prod.archived = false and
        (prod.id = prod_cat.productId) and
        (prod_cat.categoryId = cat.category_id) and
        (cat.left_value >= ${category.leftValue}) and
        (cat.right_value <= ${category.rightValue}) and
        ((${brandId} = 0) or (prod.brand_id = ${brandId})) and
        ((${search} = '') or (prod.title like ${'%'+search+'%'})) and
        ((${inStock} = FALSE) or (prod.is_available = ${inStock}))
    """

    val count = countQuery.as[Int].first
    new ListPage(pageNumber, products, count)
  }


  def get(id: Int, getBrand: Int => Option[BrandEntity]): ProductDetails = database withDynSession {
    implicit val getProductDetails = GetResult(r =>
      new ProductDetails(
        r.<<,
        r.nextStringOption.getOrElse(""),
        r.nextStringOption.getOrElse(""),
        r.<<,
        r.<<,
        r.nextIntOption.getOrElse(0),
        new BrandEntity(r.nextIntOption.getOrElse(0), r.nextStringOption.getOrElse(Messages("brand.unknown"))),
        new CategoryEntity(r.nextIntOption.getOrElse(0), r.nextStringOption.getOrElse(Messages("category.unknown"))),
        r.nextBoolean
      ))
    sql"""
      select
        p.title,
        p.description,
        p.short_description,
        p.price,
        p.id,
        p.default_image_id,
        b.id,
        b.title as brandTitle,
        pc.categoryId,
        c.title,
        p.is_available
      from
        products p
      left join products_categories pc on pc.productId = p.id
      left join categories c on c.category_id = pc.categoryId
      left join brands b on b.id = p.brand_id
      where p.id = ${id};
    """.as[ProductDetails].first()
  }

  def get(id: Int): ProductDetails = database withDynSession {
    implicit val getResult = GetResult(r => new ProductDetails(r.<<, r.nextStringOption.getOrElse(""), r.<<, r.<<, r.nextIntOption.getOrElse(0)))
    sql"""
      select
        p.title,
        p.description,
        p.price,
        p.id,
        p.default_image_id,
        p.brand_id
      from
        products p
      where
        p.id = ${id};
    """.as[ProductDetails].first
  }


  def create(details: ProductDetails, getBrandId: String => Int, userId: Int)(after: Int => Unit): Int = database withDynSession {
    val brandId = getBrandId(details.brand.title)
    sqlu"""
      insert into
        products(title, description, short_description, price, brand_id, created_by_user, is_available)
        values(${details.title},
          ${details.description},
          ${details.shortDescription},
          ${details.price},
          ${brandId},
          ${userId},
          ${details.isAvailable})
    """.execute()
    val productId = sql"select last_insert_id();".as[Int].first
    sqlu"insert into products_categories(productId, categoryId) values($productId, ${details.category.id})".execute
    after(productId)
    productId
  }

  def insertImage(imageId: Int, productId: Int) = database withDynSession {
    if (imageId > 0) {
      sqlu"""
        insert into product_images(product_id, image_id) values(${productId}, ${imageId});
        update products
        set
          default_image_id = ${imageId}
        where
          productId = ${productId} and (default_image_id = 0 or default_image_id is null)
      """.execute
    }
  }

  def update(product: ProductDetails, getBrandId: String => Int, userId: Int)(after: => Unit): Int = database withDynSession {
    sqlu"""
      update products
      set
        title = ${product.title},
        description = ${product.description},
        short_description = ${product.shortDescription},
        price = ${product.price},
        brand_id = ${getBrandId(product.brand.title)}
      where id = ${product.id}
    """.execute
    after
    product.id
  }

  def removeImage(imageId: Int, productId: Int)(after: Int => Unit) = database withDynSession {
    sqlu"""
      delete from product_images where product_id = ${productId} and image_id = ${imageId};
    """.execute
    sqlu"""
      update products
      set default_image_id = null
      where id = ${productId} and (select count(*) from product_images where product_id = ${productId}) = 0
    """.execute
    val count = sql"""
      select count(*) from product_images where image_id = ${imageId}
     """.as[Int].first()
    if (count == 0) {
      after(imageId)
    }
  }

  def imageList(productId: Int) = database withDynSession {
    implicit val getImg = GetResult(r => new ImageEntity(r.<<, r.<<, r.<<))
    sql"""
      select i.file_path, i.md5, i.image_id from product_images pi
      inner join images i on i.image_id = pi.image_id
      where pi.product_id = ${productId}
     """.as[ImageEntity].list
  }

  def delete(productId: Int)(cleanOtherResources: ImageEntity => Unit) = database withDynSession {
    val images: Seq[ImageEntity] = imageList(productId)
    images.map {
      image =>
        removeImage(image.id, productId) {
          imageId =>
            cleanOtherResources(image)
        }
    }
    sqlu"delete from products_categories where productId = ${productId}".execute
    sqlu"delete from products where id = ${productId}".execute
  }


}
