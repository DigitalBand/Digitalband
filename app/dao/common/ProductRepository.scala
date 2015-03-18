package dao.common

import models._
import models.CategoryEntity
import models.ProductDetails
import models.BrandEntity
import wt.common.image.ImageEntity

trait ProductRepository {
  def listAll(domain: String): Iterator[ProductDetails]

  def getAllNotInStockIds: Iterator[Int]

  def delete(productId: Int)(cleanOtherResources: ImageEntity => Unit)

  def removeImage(imageId: Int, productId: Int)(after: Int => Unit)

  def insertImage(imageId: Int, productId: Int)
  def update(product: ProductDetails, getBrandId: String => Int, userId: Int)(after: => Unit): Int

  def create(details: ProductDetails, getBrandId: String => Int, userId: Int)(after: Int => Unit): Int

  def get(productId: Int, getBrand: Int => Option[BrandEntity]): ProductDetails
  def get(productId: Int): ProductDetails
  def getList(getCategory: => CategoryEntity, domain: String): ListPage[ProductDetails] = getList(getCategory, brandId = 0, pageNumber = 1, pageSize = 10, search = "", inStock = false, domain)
  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int, search: String, inStock: Boolean, domain: String): ListPage[ProductDetails]
  def listMostVisited(count: Int, domain: String): Iterator[ProductDetails]
  def getAvailability(productId: Int): Int
}
