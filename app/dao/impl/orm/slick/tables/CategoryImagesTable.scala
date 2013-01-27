package dao.impl.orm.slick.tables

import dao.impl.orm.slick.common.Profile.driver.simple._

object CategoryImagesTable extends Table[(Int, Int)]("category_images"){
  def categoryId = column[Int]("categoryId")
  def imageId = column[Int]("imageId")
  def image = foreignKey("image_fk", imageId, ImagesTable)(_.id)
  def category = foreignKey("category_fk", categoryId, CategoriesTable)(_.id)
  def * = categoryId ~ imageId
  def idx = index("idx_a", (categoryId, imageId), unique = true)
}
