package dao.impl.orm.slick.tables
import scala.Double
import dao.impl.orm.slick.common.Profile.driver.simple._

object ProductsTable extends Table[(Int, String, String, String, Double, java.sql.Date, Int, Option[Int], Int, Boolean)]("products") {
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
