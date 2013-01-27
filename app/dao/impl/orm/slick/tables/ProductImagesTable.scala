package dao.impl.orm.slick.tables
import dao.impl.orm.slick.common.Profile.driver.simple._

object ProductImagesTable extends Table[(Int, Int)]("product_images") {
  def productId = column[Int]("productId")
  def imageId = column[Int]("imageId")
  def * = productId ~ imageId
}
