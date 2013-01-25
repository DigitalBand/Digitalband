package dao.common

import models.{ListPage, CategoryEntity}

trait BrandRepository {
  def list(getCategory: => CategoryEntity, pageNumber: Int, pageSize: Int): ListPage[models.BrandEntity]

}
