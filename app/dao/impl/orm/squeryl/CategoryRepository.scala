package dao.impl.orm.squeryl

import common.tables.{CategoryTable, CategoryImages}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Table
import org.squeryl.Query
import collection.Iterable
import models.CategoryEntity
import common.{Database => DB}

class CategoryRepository extends dao.common.CategoryRepository {


  def getBreadcrumbs(categoryId: Int, productId: Int, search: String) = ???

  def listWithPictures() = inTransaction {
    from(DB.categories, DB.categoryImages)((c, ci) =>
      where(c.categoryId === ci.categoryId).select(c, ci)
    ).toList.map {
      case (c: CategoryTable, ci: CategoryImages) =>
        new CategoryEntity(c.categoryId, c.title, ci.imageId, c.leftValue, c.rightValue, c.parentCategoryId.getOrElse(0))
    }
  }

  def get(id: Int) = inTransaction {
    val category = DB.categories.where(c => c.categoryId === id).single
    new CategoryEntity(category.categoryId, category.title, 0, category.leftValue, category.rightValue)
  }

  def list(categoryId: Int, brandId: Int, search: String) = ???

}
