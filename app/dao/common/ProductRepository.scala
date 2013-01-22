package dao.common

import models.{CategoryEntity, ProductUnit}

trait ProductRepository {
  def getList(getCategory: => CategoryEntity): Seq[ProductUnit] = getList(getCategory, brandId = 0, pageNumber = 1, pageSize = 10)
  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int): Seq[ProductUnit]
  def listMostVisited(count: Int): Seq[ProductUnit]
}
