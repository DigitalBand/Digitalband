package dao.impl.orm.slick

import common.{Profile, RepositoryBase}

import Profile.driver.simple._
import Database.threadLocalSession
import models.{CategoryListItem, CategoryEntity}
import tables.{ProductsCategoriesTable, ProductsTable, CategoryImagesTable, CategoriesTable}

class CategoryRepository extends RepositoryBase with dao.common.CategoryRepository {


  def listWithPictures: Seq[CategoryEntity] = {
    database withSession {
      val categories = for {
        c <- CategoriesTable
        ci <- CategoryImagesTable if c.id === ci.categoryId
      } yield (c.id, c.title, ci.imageId)
      categories.list.map {
        case (id: Int, title: String, imageId: Int) => CategoryEntity(id, title, imageId)
      }
    }
  }

  def get(id: Int): CategoryEntity = {
    database withSession {
      val categoryQuery = CategoriesTable.filter(_.id === id).map(c => c.id ~ c.title ~ c.leftValue ~ c.rightValue ~ c.parentId)
      categoryQuery.list.map {
        case (id: Int, title: String, leftValue: Int, rightValue: Int, parentCategoryId: Option[Int]) =>
          CategoryEntity(id, title, 0, leftValue, rightValue, parentCategoryId.getOrElse(0))
      }.head
    }
  }

  def list(categoryId: Int, brandId: Int, search: String): Seq[CategoryListItem] = {
    database withSession {
      def productCount(cat: CategoriesTable.type) = (for {
        pc <- ProductsCategoriesTable
        p <- ProductsTable
        c <- CategoriesTable
        if c.id === pc.categoryId &&
          p.id === pc.productId &&
          c.leftValue >= cat.leftValue &&
          c.rightValue <= cat.rightValue &&
          {if (brandId > 0) p.brandId.get === brandId else ConstColumn.TRUE === ConstColumn.TRUE} &&
          {if (!search.isEmpty) p.title.like(s"%$search%") else ConstColumn.TRUE === ConstColumn.TRUE }
      } yield (p.id.count))
      val categoryListQuery = for {
        c <- CategoriesTable
        if (c.parentId === categoryId)
      } yield (c.id, c.title, productCount(c) as "pc")
      categoryListQuery.list.map {
        case (a: Int, b: String, c: Int) => new CategoryListItem(a, b, c)
      }.filter(p => p.productCount > 0)
    }
  }

  def getBreadcrumbs(categoryId: Int, productId: Int, search: String): Seq[(Int, String)] = {
    database withSession {
      val category = get(categoryId)
      val breadCrumbsQuery = for {
        c <- CategoriesTable
        if {
          if (productId > 0) c.leftValue <= category.leftValue else c.leftValue < category.leftValue
        } && {
          if (productId > 0) c.rightValue >= category.rightValue else c.rightValue > category.rightValue
        }
      } yield (c.id, c.title)
      breadCrumbsQuery.list
    }
  }
}
