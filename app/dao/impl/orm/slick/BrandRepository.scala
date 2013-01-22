package dao.impl.orm.slick

import models.{BrandEntity, CategoryEntity}

class BrandRepository extends dao.common.BrandRepository {
  //TODO: Implement this
  def list(getCategory: => CategoryEntity, pageNumber: Int): Seq[BrandEntity] = List(BrandEntity(1, "test", 1))
}
