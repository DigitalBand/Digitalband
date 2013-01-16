package dao.impl.fake

import models.CategoryEntity

class CategoryRepository extends dao.common.CategoryRepository {
  def getListWithPictures: List[CategoryEntity] = {
    List(new CategoryEntity(1, "Test 1", 10), new CategoryEntity(2, "Test 2", 11))
  }
}
