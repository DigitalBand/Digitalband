package dao.impl.orm.slick

import common.RepositoryBase
import models._
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession
import models.CategoryEntity
import models.ProductEntity

class ProductRepository extends RepositoryBase with dao.common.ProductRepository {
  def listMostVisited(count: Int) = {
    database withSession {
      val productsQuery = ProductTable.filter(p =>
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
        p <- ProductTable
        pc <- ProductsCategories if p.id === pc.productId && (if (brandId == 0) true else p.brandId === brandId)
        c <- CategoryTable if pc.categoryId === c.id && c.leftValue >= category.leftValue && c.rightValue <= category.rightValue
      } yield (p.title, p.shortDescription, p.price, p.id, p.defaultImageId)
      play.api.Logger.info(productsQuery.selectStatement)
      val countQuery = for {
        p <- ProductTable
        pc <- ProductsCategories if p.id === pc.productId && (if (brandId == 0) true else p.brandId === brandId)
        c <- CategoryTable if pc.categoryId === c.id && c.leftValue >= category.leftValue && c.rightValue <= category.rightValue
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

  def get(id: Int): ProductEntity = ???
}
