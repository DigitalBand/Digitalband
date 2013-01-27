package dao.common

import models.{BrandEntity, ListPage, CategoryEntity}

trait BrandRepository {
  def get(id: Int): Option[BrandEntity]

  def list(getCategory: => CategoryEntity, pageNumber: Int, pageSize: Int): ListPage[models.BrandEntity]

}
