package dao.common

import models.ProductUnit

trait ProductRepository {
  def getList(): Seq[ProductUnit] = getList(categoryId = 1, brandId = 0, pageNumber = 1, pageSize = 10)
  def getList(categoryId: Int, brandId: Int, pageNumber: Int, pageSize: Int): Seq[ProductUnit]
  def listMostVisited(count: Int): Seq[ProductUnit]
}
