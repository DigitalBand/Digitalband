package models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

case class ProductUnit(title:String, description: String, price: Double, id: Int, defaultImageId: Int)

object ProductTable extends Table[(Int, String)]("products") {
  def id = column[Int]("categoryId", O.PrimaryKey, O.AutoInc)
  def path = column[String]("title")
  def * = id ~ path
}

