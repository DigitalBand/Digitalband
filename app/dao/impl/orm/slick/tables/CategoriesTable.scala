package dao.impl.orm.slick.tables
import dao.impl.orm.slick.common.Profile.driver.simple._

object CategoriesTable extends Table[(Int, String, Int, Int, Option[Int])]("categories") {
  def id = column[Int]("categoryId", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def leftValue = column[Int]("leftValue")
  def rightValue = column[Int]("rightValue")
  def parentId = column[Option[Int]]("parentCategoryId")
  def * = id ~ title ~ leftValue ~ rightValue ~ parentId
}
