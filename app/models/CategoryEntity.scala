package models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

case class CategoryEntity(id: Int, title: String, imageId: Int = 0, leftValue: Int = 0, rightValue: Int = 0)

object CategoryTable extends Table[(Int, String, Int, Int)]("categories") {
  def id = column[Int]("categoryId", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def leftValue = column[Int]("leftValue")
  def rightValue = column[Int]("rightValue")
  def * = id ~ title ~ leftValue ~ rightValue
}
