package models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

case class ProductUnit(title:String, description: String, price: Double, id: Int, defaultImageId: Int)

object ProductTable extends Table[(Int, String, String, String, Double, java.sql.Date, Int, Option[Int], Int, Boolean)]("products") {
  def id = column[Int]("productId", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def description = column[String]("description")
  def shortDescription = column[String]("shortDescription")
  def price = column[Double]("price")
  def addedDate = column[java.sql.Date]("addedDate")
  def brandId = column[Int]("brandId")
  def defaultImageId = column[Option[Int]]("defaultImageId")
  def visitCounter = column[Int]("visitCounter")
  def archived = column[Boolean]("archived")
  def * = id ~ title ~ description ~ shortDescription ~ price ~ addedDate ~ brandId ~ defaultImageId ~ visitCounter ~ archived
}

object ProductsCategories extends Table[(Int, Int)]("products_categories") {
  def productId = column[Int]("productId")
  def categoryId = column[Int]("categoryId")
  def * = productId ~ categoryId
}

