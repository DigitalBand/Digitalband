package dao.impl.orm.slick

import common.RepositoryBase
import models.{BrandEntity, CategoryEntity}

class BrandRepository extends RepositoryBase with dao.common.BrandRepository {
  //TODO: Implement this
  def list(getCategory: => CategoryEntity, pageNumber: Int): Seq[BrandEntity] = List(BrandEntity(1, "test", 1))
}
