package dao.common

import models._
import models.CategoryEntity
import models.ProductDetails
import models.BrandEntity

trait ProductRepository {


  def delete(productId: Int)(cleanOtherResources: ImageEntity => Unit)

  def removeImage(imageId: Int, productId: Int)(after: Int => Unit)

  def insertImage(imageId: Int, productId: Int)
  def update(product: ProductDetails, getBrandId: String => Int, userId: Int)(after: => Unit): Int

  def create(details: ProductDetails, getBrandId: String => Int, userId: Int)(after: Int => Unit): Int

  def get(productId: Int, getBrand: Int => Option[BrandEntity]): ProductDetails
  def get(productId: Int): ProductDetails
  def getList(getCategory: => CategoryEntity): ListPage[ProductDetails] = getList(getCategory, brandId = 0, pageNumber = 1, pageSize = 10, search = "")
  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int, search: String): ListPage[ProductDetails]
  def listMostVisited(count: Int): Seq[ProductDetails]
}
