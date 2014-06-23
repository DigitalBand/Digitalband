package dao.common

import models.{CategoryListItem, CategoryEntity}

trait CategoryRepository {
  def listAll: Seq[CategoryEntity]

  def getBreadcrumbs(categoryId: Int, productId: Int, search: String): Seq[(Int, String)]

  def listWithPictures: Seq[CategoryEntity]
  def get(id: Int): CategoryEntity
  def list(categoryId: Int, brandId: Int, search:String, inStock: Boolean): Seq[CategoryListItem]
}
