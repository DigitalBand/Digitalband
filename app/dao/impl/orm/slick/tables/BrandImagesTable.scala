package dao.impl.orm.slick.tables

import dao.impl.orm.slick.common.Profile.driver.simple._
import Database.threadLocalSession

object BrandImagesTable extends Table[(Int, Int)]("brands") {
  def brandId = column[Int]("brandId")
  def imageId = column[Int]("imageId")
  def * = brandId ~ imageId
}
