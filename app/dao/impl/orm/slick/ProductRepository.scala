package dao.impl.orm.slick

import common.RepositoryBase
import models.{ProductTable, ProductUnit}
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession



class ProductRepository extends RepositoryBase with dao.common.ProductRepository {
  def getList(): Seq[ProductUnit] = {
    List(
      new models.ProductUnit(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        1, 1),
      new models.ProductUnit(
        "IBANEZ GART60 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
        8351,
        2, 2),
      new models.ProductUnit(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        3, 3),
      new models.ProductUnit(
        "IBANEZ GART60 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
        8351,
        2, 0))
  }

  def listMostVisited(count: Int) = {
    database withSession {
      val productsQuery = ProductTable.filter(p =>
        p.archived === false && p.price > 0.0
      ).sortBy(_.visitCounter.desc).map(p => p.title ~ p.description ~ p.price ~ p.id ~ p.defaultImageId).take(count)
      val statement = productsQuery.selectStatement
      play.api.Logger.info(statement)
      productsQuery.list.map {
        product =>
          product match {
            case (title: String, descr: String, price: Double, id: Int, imageId: Option[Int]) => ProductUnit(title, descr, price, id, imageId.getOrElse(0))
          }
      }
    }
  }
}
