package dao.impl.orm.slick.tables
import dao.impl.orm.slick.common.Profile.driver.simple._

object ProductsCategoriesTable extends Table[(Int, Int)]("products_categories") {
  def productId = column[Int]("productId")
  def categoryId = column[Int]("categoryId")
  def * = productId ~ categoryId
}
