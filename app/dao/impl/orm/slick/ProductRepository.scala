package dao.impl.orm.slick

import common._
import models._
import Profile.driver.simple._
import Database.threadLocalSession
import models.CategoryEntity
import models.ProductEntity
import tables.{ProductsTable, ProductsCategoriesTable, CategoriesTable}

class ProductRepository extends RepositoryBase with dao.common.ProductRepository {
  def listMostVisited(count: Int) = {
    database withSession {
      val productsQuery = ProductsTable.filter(p =>
        p.archived === false && p.price > 0.0
      ).sortBy(_.visitCounter.desc).take(count).map(p => p.title ~ p.description ~ p.price ~ p.id ~ p.defaultImageId)
      productsQuery.list.map {
          case (title: String, descr:Option[String], price:Double, id:Int, imageId:Option[Int]) =>
            ProductEntity(title, descr.getOrElse(""), price, id, imageId.getOrElse(0))
      }
    }
  }

  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int, search: String): ListPage[ProductEntity] = {
    val category = getCategory
    database withSession {
      val productsQuery = for {
        p <- ProductsTable
        pc <- ProductsCategoriesTable if p.id === pc.productId &&
        {if (brandId == 0) ConstColumn.TRUE === ConstColumn.TRUE else p.brandId.get is brandId}
        c <- CategoriesTable
        if pc.categoryId === c.id &&
          c.leftValue >= category.leftValue &&
          c.rightValue <= category.rightValue  &&
          (if (!search.isEmpty()) p.title like "%"+search+"%" else ConstColumn.TRUE === ConstColumn.TRUE)
      } yield (p.title, p.shortDescription, p.price, p.id, p.defaultImageId)
      val countQuery = for {
        p <- ProductsTable
        pc <- ProductsCategoriesTable if p.id === pc.productId && (if (brandId == 0) true else p.brandId.get === brandId)
        c <- CategoriesTable if pc.categoryId === c.id && c.leftValue >= category.leftValue && c.rightValue <= category.rightValue &&
        (if (!search.isEmpty()) p.title like "%"+search+"%" else true)
      } yield (p.title.count)
      val count = countQuery.firstOption.getOrElse(0)
      val products = productsQuery.drop(pageSize*(pageNumber-1)).take(pageSize).list.map {
        case (title: String, Some(descr: String), price: Double, id: Int, Some(imageId:Int)) =>
          ProductEntity(title, descr, price, id, imageId)
        case (title: String, Some(descr: String), price: Double, id: Int, None) =>
          ProductEntity(title, descr, price, id, 0)
        case (title: String, None, price: Double, id: Int, None) =>
          ProductEntity(title, "", price, id, 0)
        case (title: String, None, price: Double, id: Int, Some(imageId:Int)) =>
          ProductEntity(title, "", price, id, imageId)
      }
      new ListPage(pageNumber, products, count)
    }
  }

  def get(id: Int, getBrand: Int => Option[BrandEntity]): ProductDetails = {
    database withSession {
      val productQuery = ProductsTable.filter(p => p.id === id).map(p => p.title ~ p.description ~ p.price ~ p.defaultImageId ~ p.brandId)
      productQuery.firstOption.map {
        case (title: String, description: Option[String], price: Double, defaultImageId: Option[Int], brandId: Option[Int]) =>
          ProductDetails(title, description.getOrElse(""), price, id, defaultImageId.getOrElse(0),
            brandId match {case Some(x) => getBrand(x).get case None => new BrandEntity(0, "Error", 0, 0) })
      }.get
    }
  }

  def get(id: Int): ProductEntity = {
    database withSession {
      val productQuery = ProductsTable.filter(p => p.id === id).map(p => p.title ~ p.description ~ p.price ~ p.defaultImageId ~ p.brandId)
      productQuery.firstOption.map {
        case (title: String, description: Option[String], price: Double, defaultImageId: Option[Int], brandId: Option[Int]) =>
          ProductEntity(title, description.getOrElse(""), price, id, defaultImageId.getOrElse(0))
      }.get
    }
  }
}
