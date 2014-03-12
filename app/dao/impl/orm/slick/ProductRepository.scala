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
        p.shortDescription,
        p.price,
        p.productId,
        p.defaultImageId
      from
        products p
      where
        p.archived = false and
        p.price > 0 and
        p.isAvailable = true
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
        prod.`title`,
        prod.`shortDescription`,
        prod.`price`,
        prod.`productId`,
        prod.`defaultImageId`,
        prod.isAvailable
      from
        `products` prod,
        `products_categories` prod_cat,
        `categories` cat
      where
        prod.archived = false and
        (prod.`productId` = prod_cat.`productId`) and
        (prod_cat.`categoryId` = cat.`categoryId`) and
        (cat.`leftValue` >= ${category.leftValue}) and
        (cat.`rightValue` <= ${category.rightValue}) and
        ((${brandId} = 0) or (prod.brand_id = ${brandId})) and
        ((${search} = '') or (prod.title like ${'%'+search+'%'})) and
        ((${inStock} = FALSE) or (prod.isAvailable = ${inStock}))
      limit ${(pageNumber - 1)*pageSize}, ${pageSize}
    """.as[ProductDetails].list
    val countQuery = sql"""
      select
        count(prod.`title`)
      from
        `products` prod,
        `products_categories` prod_cat,
        `categories` cat
      where
        prod.archived = false and
        (prod.`productId` = prod_cat.`productId`) and
        (prod_cat.`categoryId` = cat.`categoryId`) and
        (cat.`leftValue` >= ${category.leftValue}) and
        (cat.`rightValue` <= ${category.rightValue}) and
        ((${brandId} = 0) or (prod.brand_id = ${brandId})) and
        ((${search} = '') or (prod.title like ${'%'+search+'%'})) and
        ((${inStock} = FALSE) or (prod.isAvailable = ${inStock}))
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
        p.shortDescription,
        p.price,
        p.productId,
        p.defaultImageId,
        b.id,
        b.title as brandTitle,
        pc.categoryId,
        c.title,
        p.isAvailable
      from
        products p
      left join products_categories pc on pc.productId = p.productId
      left join categories c on c.CategoryId = pc.categoryId
      left join brands b on b.id = p.brand_id
      where p.productId = $id;
    """.as[ProductDetails].first()
  }

  def get(id: Int): ProductDetails = database withDynSession {
    implicit val getResult = GetResult(r => new ProductDetails(r.<<, r.nextStringOption.getOrElse(""), r.<<, r.<<, r.nextIntOption.getOrElse(0)))
    sql"""
      select
        p.title,
        p.description,
        p.price,
        p.defaultImageId,
        p.brand_id
      from
        products p
      where
        p.productId = id;
    """.as[ProductDetails].first
  }


  def create(details: ProductDetails, getBrandId: String => Int, userId: Int)(after: Int => Unit): Int = database withDynSession {
    val brandId = getBrandId(details.brand.title)
    sqlu"""
      insert into
        products(title, description, shortDescription, price, brand_id, createdByUser, isAvailable)
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
        insert into product_images(productId, imageId) values($productId, $imageId);
        update products
        set
          defaultImageId = $imageId
        where
          productId = $productId and (defaultImageId = 0 or defaultImageId is null)
      """.execute
    }
  }

  def update(product: ProductDetails, getBrandId: String => Int, userId: Int)(after: => Unit): Int = database withDynSession {
    sqlu"""
      update products
      set
        title = ${product.title},
        description = ${product.description},
        shortDescription = ${product.shortDescription},
        price = ${product.price},
        brand_id = ${getBrandId(product.brand.title)}
      where productId = ${product.id}
    """.execute
    after
    product.id
  }

  def removeImage(imageId: Int, productId: Int)(after: Int => Unit) = database withDynSession {
    sqlu"""
      delete from product_images where productId = $productId and imageId = $imageId;
    """.execute
    sqlu"""
      update products
      set defaultImageId = null
      where productId = ${productId} and (select count(*) from product_images where productId = ${productId}) = 0
    """.execute
    val count = sql"""
      select count(*) from product_images where imageId = $imageId
     """.as[Int].first()
    if (count == 0) {
      after(imageId)
    }
  }

  def imageList(productId: Int) = database withDynSession {
    implicit val getImg = GetResult(r => new ImageEntity(r.<<, r.<<, r.<<))
    sql"""
      select i.filePath, i.md5, i.imageId from product_images pi
      inner join images i on i.imageId = pi.imageId
      where productId = $productId
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
    sqlu"delete from products_categories where productId = $productId".execute
    sqlu"delete from products where productId = $productId".execute
  }


}
