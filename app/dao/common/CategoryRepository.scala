package dao.common

import models.{CategoryListItem, CategoryEntity}

import scala.concurrent.Future

trait CategoryRepository {
  def listAll: Future[Seq[CategoryEntity]]

  def getBreadcrumbs(categoryId: Int, productId: Int, search: String): Future[Seq[(Int, String)]]

  def listWithPictures: Future[Seq[CategoryEntity]]
  def get(id: Int): Future[CategoryEntity]
  def list(categoryId: Int, brandId: Int, search:String, inStock: Boolean, domain: String): Future[Seq[CategoryListItem]]
}
