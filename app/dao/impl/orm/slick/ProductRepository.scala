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
      play.api.Logger.info(productsQuery.selectStatement)
      productsQuery.list.map {
          case (title: String, descr:String, price:Double, id:Int, imageId:Option[Int]) =>
            ProductEntity(title, descr, price, id, imageId.getOrElse(0))
      }
    }
  }
  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int): ListPage[ProductEntity] = {
    val category = getCategory
    database withSession {
      val productsQuery = for {
        p <- ProductsTable
        pc <- ProductsCategoriesTable if p.id === pc.productId && (if (brandId == 0) true else p.brandId === brandId)
        c <- CategoriesTable if pc.categoryId === c.id && c.leftValue >= category.leftValue && c.rightValue <= category.rightValue
      } yield (p.title, p.shortDescription, p.price, p.id, p.defaultImageId)
      play.api.Logger.info(productsQuery.selectStatement)
      val countQuery = for {
        p <- ProductsTable
        pc <- ProductsCategoriesTable if p.id === pc.productId && (if (brandId == 0) true else p.brandId === brandId)
        c <- CategoriesTable if pc.categoryId === c.id && c.leftValue >= category.leftValue && c.rightValue <= category.rightValue
      } yield (p.title.count)
      val count = countQuery.firstOption.getOrElse(0)
      play.api.Logger.info(countQuery.selectStatement)
      val products = productsQuery.drop(pageSize*(pageNumber-1)).take(pageSize).list.map {
        case (title: String, descr: String, price: Double, id: Int, imageId:Option[Int]) =>
          ProductEntity(title, descr, price, id, imageId.getOrElse(0))
      }
      new ListPage(pageNumber, products, count)
    }
  }

  def get(id: Int): ProductEntity = {
    database withSession {
      val productQuery = ProductsTable.filter(p => p.id === id).map(p => p.title ~ p.description ~ p.price ~ p.defaultImageId)
      productQuery.firstOption.map {
        case (title: String, description: String, price: Double, defaultImageId: Option[Int]) =>
          ProductEntity(title, description, price, id, defaultImageId.getOrElse(0))
      }.get
    }
  }
}
