package dao.common

import models._
import models.CategoryEntity
import models.{ProductDetails => ProductEntity}
import models.BrandEntity

trait ProductRepository {
  def get(productId: Int, getBrand: Int => Option[BrandEntity]): ProductDetails
  def get(productId: Int): ProductEntity
  def getList(getCategory: => CategoryEntity): ListPage[ProductEntity] = getList(getCategory, brandId = 0, pageNumber = 1, pageSize = 10, search = "")
  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int, search: String): ListPage[ProductEntity]
  def listMostVisited(count: Int): Seq[ProductEntity]
}
