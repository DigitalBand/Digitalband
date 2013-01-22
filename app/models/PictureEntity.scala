package models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

case class PictureEntity (val id: Int, val path: String, val imageType: String)

object ImageTable extends Table[(Int, String)]("images") {
  def id = column[Int]("imageId", O.PrimaryKey, O.AutoInc)
  def path = column[String]("filePath")
  def * = id ~ path
}

object CategoryImages extends Table[(Int, Int)]("category_images"){
  def categoryId = column[Int]("categoryId")
  def imageId = column[Int]("imageId")
  def image = foreignKey("image_fk", imageId, ImageTable)(_.id)
  def category = foreignKey("category_fk", categoryId, CategoryTable)(_.id)
  def * = categoryId ~ imageId
  def idx = index("idx_a", (categoryId, imageId), unique = true)
}
