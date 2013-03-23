package dao.common

import models._
import models.CategoryEntity
import models.ProductDetails
import models.BrandEntity

trait ProductRepository {
  def create(details: ProductDetails, imageId: Int, getBrandId: String => Int, userId: Int): Int

  def get(productId: Int, getBrand: Int => Option[BrandEntity]): ProductDetails
  def get(productId: Int): ProductDetails
  def getList(getCategory: => CategoryEntity): ListPage[ProductDetails] = getList(getCategory, brandId = 0, pageNumber = 1, pageSize = 10, search = "")
  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int, search: String): ListPage[ProductDetails]
  def listMostVisited(count: Int): Seq[ProductDetails]
}
