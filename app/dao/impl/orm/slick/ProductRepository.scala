package dao.impl.orm.slick

import common.RepositoryBase
import models.{ProductsCategories, ProductTable, ProductUnit}
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

class ProductRepository extends RepositoryBase with dao.common.ProductRepository {
  def listMostVisited(count: Int) = {
    database withSession {
      val productsQuery = ProductTable.filter(p =>
        p.archived === false && p.price > 0.0
      ).sortBy(_.visitCounter.desc).take(count).map(p => p.title ~ p.description ~ p.price ~ p.id ~ p.defaultImageId)
      play.api.Logger.info(productsQuery.selectStatement)
      productsQuery.list.map {
          case (title: String, descr:String, price:Double, id:Int, imageId:Option[Int]) =>
            ProductUnit(title, descr, price, id, imageId.getOrElse(0))
      }
    }
  }
  def getList(categoryId: Int, brandId: Int, pageNumber: Int, pageSize: Int): Seq[ProductUnit] = {
    /*database withSession {
      val productsQuery = for {
        p <- ProductTable
        c <- ProductsCategories if p.id === c.productId && p.brandId === brandId && c.categoryId
      } yield ()
    }*/
    ???
  }
}
