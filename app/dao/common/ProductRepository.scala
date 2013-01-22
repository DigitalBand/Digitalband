package dao.common

import models.{ListPage, CategoryEntity, ProductEntity}

trait ProductRepository {
  def get(productId: Int): ProductEntity
  def getList(getCategory: => CategoryEntity): ListPage[ProductEntity] = getList(getCategory, brandId = 0, pageNumber = 1, pageSize = 10)
  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int): ListPage[ProductEntity]
  def listMostVisited(count: Int): Seq[ProductEntity]
}
