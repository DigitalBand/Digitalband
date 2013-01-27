package dao.common

import models.{CategoryListItem, CategoryEntity}

trait CategoryRepository {
  def getListWithPictures(): Seq[CategoryEntity]
  def get(id: Int): CategoryEntity
  def list(categoryId: Int, brandId: Int): Seq[CategoryListItem]
}
