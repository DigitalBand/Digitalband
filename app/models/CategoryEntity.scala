package models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

case class CategoryEntity(val id: Int, val title: String, val imageId: Int)

object CategoryTable extends Table[(Int, String)]("categories") {
  def id = column[Int]("categoryId", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def * = id ~ title
}
