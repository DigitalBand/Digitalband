package dao.impl.orm.slick.tables

import dao.impl.orm.slick.common.Profile.driver.simple._

object BrandImagesTable extends Table[(Int, Int)]("brand_images") {
  def brandId = column[Int]("brandId")
  def imageId = column[Int]("imageId")
  def * = brandId ~ imageId
}


