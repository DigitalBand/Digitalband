package dao.common

import models.CategoryEntity

trait BrandRepository {
  def list(getCategory: => CategoryEntity, pageNumber: Int): Seq[models.BrandEntity]

}
