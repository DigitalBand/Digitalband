package dao.impl.orm.slick

import common._
import models._
import Profile.driver.simple._
import Database.threadLocalSession
import models.CategoryEntity
import models.ProductDetails
import tables.{ProductsTable, ProductsCategoriesTable, CategoriesTable}
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import play.api.i18n.Messages

class ProductRepository extends RepositoryBase with dao.common.ProductRepository {
  def listMostVisited(count: Int) = database withSession {
    val productsQuery = ProductsTable.filter(p =>
      p.archived === false && p.price > 0.0
    ).sortBy(_.visitCounter.desc).take(count).map(p => p.title ~ p.description ~ p.price ~ p.id ~ p.defaultImageId)
    productsQuery.list.map {
      case (title: String, descr: Option[String], price: Double, id: Int, imageId: Option[Int]) =>
        new ProductDetails(title, descr.getOrElse(""), price, id, imageId.getOrElse(0))
    }
  }


  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int, search: String): ListPage[ProductDetails] = database withSession {
    val category = getCategory
    val productsQuery = for {
      p <- ProductsTable
      pc <- ProductsCategoriesTable if p.id === pc.productId && {
      if (brandId == 0) ConstColumn.TRUE === ConstColumn.TRUE else p.brandId.get is brandId
    }
      c <- CategoriesTable
      if pc.categoryId === c.id &&
        c.leftValue >= category.leftValue &&
        c.rightValue <= category.rightValue &&
        (if (!search.isEmpty()) p.title like "%" + search + "%" else ConstColumn.TRUE === ConstColumn.TRUE)
    } yield (p.title, p.shortDescription, p.price, p.id, p.defaultImageId)
    val countQuery = for {
      p <- ProductsTable
      pc <- ProductsCategoriesTable if p.id === pc.productId && (if (brandId == 0) true else p.brandId.get === brandId)
      c <- CategoriesTable if pc.categoryId === c.id && c.leftValue >= category.leftValue && c.rightValue <= category.rightValue &&
      (if (!search.isEmpty()) p.title like "%" + search + "%" else true)
    } yield (p.title.count)
    val count = countQuery.firstOption.getOrElse(0)
    val products = productsQuery.drop(pageSize * (pageNumber - 1)).take(pageSize).list.map {
      case (title: String, Some(descr: String), price: Double, id: Int, Some(imageId: Int)) =>
        new ProductDetails(title, descr, price, id, imageId)
      case (title: String, Some(descr: String), price: Double, id: Int, None) =>
        new ProductDetails(title, descr, price, id, 0)
      case (title: String, None, price: Double, id: Int, None) =>
        new ProductDetails(title, "", price, id, 0)
      case (title: String, None, price: Double, id: Int, Some(imageId: Int)) =>
        new ProductDetails(title, "", price, id, imageId)
    }
    new ListPage(pageNumber, products, count)
  }


  def get(id: Int, getBrand: Int => Option[BrandEntity]): ProductDetails = database withSession {
    implicit val getProductDetails = GetResult(r =>
      new ProductDetails(
        r.nextString,
        r.nextStringOption.getOrElse(""),
        r.nextStringOption.getOrElse(""),
        r.nextDouble,
        r.nextInt,
        r.nextIntOption.getOrElse(0),
        new CategoryEntity(r.nextIntOption.getOrElse(0), r.nextStringOption.getOrElse(Messages("category.unknown"))),
        new BrandEntity(r.nextIntOption.getOrElse(0), r.nextStringOption.getOrElse(Messages("brand.unknown")))))
    sql"""
      select
        p.title,
        p.description,
        p.shortDescription,
        p.price,
        p.productId,
        p.defaultImageId,
        pc.categoryId,
        c.title,
        b.brandId,
        b.title as brandTitle
      from
        products p
      left join products_categories pc on pc.productId = p.productId
      left join categories c on c.CategoryId = pc.categoryId
      left join brands b on b.brandId = p.brandId
      where p.productId = $id;
    """.as[ProductDetails].first()
  }

  def get(id: Int): ProductDetails = database withSession {
    val productQuery = ProductsTable.filter(p => p.id === id).map(p => p.title ~ p.description ~ p.price ~ p.defaultImageId ~ p.brandId)
    productQuery.firstOption.map {
      case (title: String, description: Option[String], price: Double, defaultImageId: Option[Int], brandId: Option[Int]) =>
        new ProductDetails(title, description.getOrElse(""), price, id, defaultImageId.getOrElse(0))
    }.get
  }


  def create(details: ProductDetails, imageId: Int, getBrandId: String => Int, userId:Int): Int = database withSession {
    sqlu"""
      insert into
        products(title, description, shortDescription, price, brandId, defaultImageId, createdByUser)
        values(${details.title},
          ${details.description},
          ${details.shortDescription},
          ${details.price},
          ${getBrandId(details.brand.title)},
          ${imageId},
          ${userId})
    """.execute
    val productId = sql"select last_insert_id();".as[Int].first
    sqlu"insert into products_categories(productId, categoryId) values($productId, ${details.category.id})".execute
    sqlu"""
      insert into product_images(productId, imageId) values($productId, $imageId)
    """
    productId
  }
}
