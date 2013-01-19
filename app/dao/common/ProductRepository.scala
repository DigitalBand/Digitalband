package dao.common

import models.ProductUnit

trait ProductRepository {
  def getList(): Seq[ProductUnit]
  def listMostVisited(count: Int): Seq[ProductUnit]
}
