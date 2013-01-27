package dao.impl.orm.slick.tables

import dao.impl.orm.slick.common.Profile.driver.simple._

object ImagesTable extends Table[(Int, String)]("images") {
  def id = column[Int]("imageId", O.PrimaryKey, O.AutoInc)
  def path = column[String]("filePath")
  def * = id ~ path
}
