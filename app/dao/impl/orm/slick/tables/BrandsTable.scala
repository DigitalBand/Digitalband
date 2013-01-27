package dao.impl.orm.slick.tables

import dao.impl.orm.slick.common.Profile.driver.simple._

object BrandsTable extends Table[(Int, String)]("brands") {
  def id = column[Int]("brandId", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def * = id ~ title
}
