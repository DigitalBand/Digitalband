package dao.common

import models._
import models.CategoryEntity
import models.ProductDetails
import models.BrandEntity
import wt.common.image.ImageEntity

import scala.concurrent.Future

trait ProductRepository {
  def listAll(domain: String): Future[Seq[ProductDetails]]

  def getAllNotInStockIds: Future[Seq[Int]]

  def delete(productId: Int)(cleanOtherResources: ImageEntity => Unit): Future[Int]

  def removeImage(imageId: Int, productId: Int)(after: Int => Unit): Future[Int]

  def insertImage(imageId: Int, productId: Int): Future[Int]

  def update(product: ProductDetails, brandId: Int, userId: Int)(after: => Unit): Future[Int]

  def create(details: ProductDetails, brandId: Int, userId: Int)(after: Int => Unit): Future[Int]

  def get0(productId: Int): Future[ProductDetails]

  def get(productId: Int): Future[ProductDetails]

  def getList(getCategory: => CategoryEntity, domain: String): Future[ListPage[ProductDetails]] =
    getList(getCategory, brandId = 0, pageNumber = 1, pageSize = 10, search = "", inStock = false, domain)

  def getList(getCategory: => CategoryEntity, brandId: Int,
              pageNumber: Int, pageSize: Int, search: String,
              inStock: Boolean, domain: String): Future[ListPage[ProductDetails]]

  def listMostVisited(count: Int, domain: String): Future[Seq[ProductDetails]]

  def getAvailability(productId: Int): Future[Int]
}
