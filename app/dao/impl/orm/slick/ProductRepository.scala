package dao.impl.orm.slick

import common._
import models._
import slick.driver.MySQLDriver.api._
import models.CategoryEntity
import models.ProductDetails
import slick.jdbc.GetResult
import play.api.i18n.Messages
import wt.common.image.ImageEntity
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ProductRepository extends RepositoryBase with dao.common.ProductRepository {
  def listMostVisited(count: Int, domain: String): Future[Seq[ProductDetails]] = usingDB {
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
        ((
          select sum(quantity) from stock_items si
          left join shops s on s.id = si.shop_id
          left join cities c on c.id = s.city_id
          where
            product_id = p.id and c.domain = ${domain}
        ) > 0)
      limit ${count};
    """.as[ProductDetails]
  }

  override def getAvailability(productId: Int): Future[Int] = usingDB {
    sql"""
      select sum(quantity) from stock_items where product_id = ${productId};
    """.as[Int].head
  }

  def getList(getCategory: => CategoryEntity,
              brandId: Int, pageNumber: Int,
              pageSize: Int, search: String, inStock: Boolean,
              domain: String): Future[ListPage[ProductDetails]] = {
    implicit val getProducts = GetResult(
      r => new ProductDetails(r.nextString, r.nextString, r.nextDouble, r.nextInt, r.nextInt, r.nextBoolean))
    val category = getCategory

    val productsFuture = usingDB {
      sql"""
      select
        prod.title,
        prod.short_description,
        prod.price,
        prod.id,
        prod.default_image_id,
        prod.is_available
      from
        products prod,
        products_categories prod_cat,
        categories cat
      where
        prod.archived = false and
        (prod.id = prod_cat.product_id) and
        (prod_cat.category_id = cat.category_id) and
        (cat.left_value >= ${category.leftValue}) and
        (cat.right_value <= ${category.rightValue}) and
        ((${brandId} = 0) or (prod.brand_id = ${brandId})) and
        ((${search} = '') or (prod.title like ${'%' + search + '%'})) and

        ((${inStock} = FALSE) or ((
                                    select sum(quantity) from stock_items si
                                    left join shops s on s.id = si.shop_id
                                    left join cities c on c.id = s.city_id
                                    where
                                      product_id = prod.id and c.domain = ${domain}
                                  ) > 0))
      limit ${(pageNumber - 1) * pageSize}, ${pageSize}
    """.as[ProductDetails]
    }

    val countQuery = usingDB {
      sql"""
        select
          count(prod.title)
        from
          products prod,
          products_categories prod_cat,
          categories cat
        where
          prod.archived = false and
          (prod.id = prod_cat.product_id) and
          (prod_cat.category_id = cat.category_id) and
          (cat.left_value >= ${category.leftValue}) and
          (cat.right_value <= ${category.rightValue}) and
          ((${brandId} = 0) or (prod.brand_id = ${brandId})) and
          ((${search} = '') or (prod.title like ${'%' + search + '%'})) and
          ((${inStock} = FALSE) or ((
                                      select sum(quantity) from stock_items si
                                      left join shops s on s.id = si.shop_id
                                      left join cities c on c.id = s.city_id
                                      where
                                        product_id = prod.id and c.domain = ${domain}
                                    ) > 0))
      """.as[Int].head
    }
    for {
      products <- productsFuture
      count <- countQuery
    } yield new ListPage(pageNumber, products, count)
  }

  def get0(id: Int): Future[ProductDetails] = usingDB {
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
        b.title as brand_title,
        pc.category_id,
        c.title,
        p.is_available
      from
        products p
      left join products_categories pc on pc.product_id = p.id
      left join categories c on c.category_id = pc.category_id
      left join brands b on b.id = p.brand_id
      where p.id = ${id};
    """.as[ProductDetails].head
  }

  def get(id: Int): Future[ProductDetails] = usingDB {
    implicit val getResult = GetResult(r => new ProductDetails(
      r.<<,
      r.nextStringOption.getOrElse(""),
      r.<<,
      r.<<,
      r.nextIntOption.getOrElse(0))
    )
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
    """.as[ProductDetails].head
  }

  def create(details: ProductDetails, brandId: Int, userId: Int)(after: Int => Unit): Future[Int] = {
    val productIdFuture = usingDB {
      sql"""
      insert into
        products(title, description, short_description, price, brand_id, created_by_user, is_available)
        values(${details.title},
          ${details.description},
          ${details.shortDescription},
          ${details.price},
          ${brandId},
          ${userId},
          ${details.isAvailable});
        select last_insert_id();
    """.as[Int].head
    }
    def insertedCountFuture(productId: Int) = usingDB {
      sql"""
      insert into products_categories(product_id, category_id) values($productId, ${details.category.id})
    """.as[Int].head
    }
    for {
      productId <- productIdFuture
      insertedIntoProductCategoriesCount <- insertedCountFuture(productId)
    } yield {
      after(productId)
      productId
    }
  }

  def insertImage(imageId: Int, productId: Int): Future[Int] = if (imageId > 0) {
    usingDB {
      sql"""
        insert into product_images(product_id, image_id) values(${productId}, ${imageId});
        update products
        set
          default_image_id = ${imageId}
        where
          id = ${productId} and (default_image_id = 0 or default_image_id is null)
      """.as[Int].head
    }
  } else Future(0)

  def update(product: ProductDetails, brandId: Int, userId: Int)(after: => Unit): Future[Int] = {
    usingDB {
      sql"""
      update products
      set
        title = ${product.title},
        description = ${product.description},
        short_description = ${product.shortDescription},
        price = ${product.price},
        brand_id = ${brandId}
      where id = ${product.id}
    """.as[Int].head
    }.map { count =>
      after
      product.id
    }
  }

  def removeImage(imageId: Int, productId: Int)(after: Int => Unit): Future[Int] = {
    val updatedProductImagesCountFuture = usingDB {
      DBIO.sequence(Seq(
      sql"""
        delete from product_images where product_id = ${productId} and image_id = ${imageId};
      """.as[Int].head,
      sql"""
        update products
        set default_image_id = null
        where id = ${productId} and (select count(*) from product_images where product_id = ${productId}) = 0
      """.as[Int].head
      )).map(_.sum)
    }
    val countFuture = usingDB {
      sql"""
      select count(*) from product_images where image_id = ${imageId}
     """.as[Int].head
    }
    for {
      updatedCount <- updatedProductImagesCountFuture
      count <- countFuture
    } yield {
      if (count == 0) {
        after(imageId)
      }
      updatedCount
    }
  }

  def imageList(productId: Int): Future[Seq[ImageEntity]] = usingDB {
    implicit val getImg = GetResult(r => new ImageEntity(r.<<, r.<<, r.<<))
    sql"""
      select i.file_path, i.md5, i.image_id from product_images pi
      inner join images i on i.image_id = pi.image_id
      where pi.product_id = ${productId}
     """.as[ImageEntity]
  }

  def delete(productId: Int)(cleanOtherResources: ImageEntity => Unit): Future[Int] = {
    val deletedCountFuture = usingDB {
      DBIO.sequence(Seq(
        sql"delete from products_categories where product_id = ${productId}".as[Int].head,
        sql"delete from products where id = ${productId}".as[Int].head
      )).map(_.sum)
    }
    for {
      deletedCount <- deletedCountFuture
      images <- imageList(productId)
    } yield {
      images.map {
        image =>
          removeImage(image.id, productId) {
            imageId =>
              cleanOtherResources(image)
          }
      }
      deletedCount
    }
  }

  override def getAllNotInStockIds = usingDB {
    sql"""
      select p.id from products p where p.id not in (select si.product_id from stock_items si where si.product_id = p.id)
    """.as[Int]
  }

  def listAll(domain: String) = usingDB {
    implicit val getProductDetailsResult = GetResult(r => ProductDetails(
      id = r.<<,
      title = r.<<,
      description = r.<<,
      shortDescription = r.<<,
      price = r.<<,
      brandName = r.<<,
      categoryId = r.<<,
      isAvailable = r.nextBooleanOption().getOrElse(false),
      defaultImageId = r.<<
    ))
    sql"""
      select
        p.id,
        p.title,
        p.description,
        p.short_description,
        p.price,
        b.title,
        pc.category_id,
        (
          select sum(ifnull(si.quantity, 0)) from stock_items si
          left join shops s on s.id = si.shop_id
          left join cities c on c.id = s.city_id
          where
            product_id = p.id and c.domain = ${domain}
        ) > 0 as is_available,
        p.default_image_id
      from
        products p
      inner join brands b on p.brand_id = b.id
      inner join products_categories pc on pc.product_id = p.id
      where archived = false;
    """.as[ProductDetails]
  }
}
