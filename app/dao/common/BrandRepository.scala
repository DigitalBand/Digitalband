package dao.common

import models.{BrandEntity, ListPage, CategoryEntity}

trait BrandRepository {
  def getBrandId(name: String): Int

  def get(id: Int): Option[BrandEntity]

  def list(getCategory: => CategoryEntity, pageNumber: Int, pageSize: Int, search: String, inStock: Boolean = false): ListPage[models.BrandEntity]

}
