package dao.common

import models.{BrandEntity, ListPage, CategoryEntity}

import scala.concurrent.Future

trait BrandRepository {
  def getBrandId(name: String): Future[Int]

  def get(id: Int): Future[Option[BrandEntity]]

  def list(getCategory: => CategoryEntity,
           pageNumber: Int,
           pageSize: Int,
           search: String, inStock: Boolean = false, domain: String): Future[ListPage[models.BrandEntity]]

}
