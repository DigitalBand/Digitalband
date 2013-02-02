package dao.common

import models.{CategoryListItem, CategoryEntity}

trait CategoryRepository {
  def getBreadcrumbs(categoryId: Int, productId: Int): Seq[(Int, String)]

  def listWithPictures(): Seq[CategoryEntity]
  def get(id: Int): CategoryEntity
  def list(categoryId: Int, brandId: Int): Seq[CategoryListItem]
}
