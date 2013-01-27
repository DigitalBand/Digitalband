package dao.impl.fake

import models.CategoryEntity

class CategoryRepository extends dao.common.CategoryRepository {
  def listWithPictures: Seq[CategoryEntity] = {
    List(new CategoryEntity(1, "Test 1", 4), new CategoryEntity(2, "Test 2", 5))
  }

  def get(id: Int): CategoryEntity = CategoryEntity(id, "Test Category")

  def list(categoryId: Int, brandId: Int) = ???
}
