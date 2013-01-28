package dao.common

import models._
import models.CategoryEntity
import models.ProductEntity
import models.BrandEntity

trait ProductRepository {
  def get(productId: Int, getBrand: Int => Option[BrandEntity]): ProductDetails
  def getList(getCategory: => CategoryEntity): ListPage[ProductEntity] = getList(getCategory, brandId = 0, pageNumber = 1, pageSize = 10)
  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int): ListPage[ProductEntity]
  def listMostVisited(count: Int): Seq[ProductEntity]
}
