package dao.impl.orm.slick

import common.{Profile, RepositoryBase}

import Profile.driver.simple._
import Database.threadLocalSession

import slick.jdbc.{GetResult, StaticQuery => Q}

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

  def list(categoryId: Int, brandId: Int, search:String): Seq[CategoryListItem] = {
    database withSession {
      def subQuery(cat: CategoriesTable.type) = (for {
        pc <- ProductsCategoriesTable
        p <- ProductsTable
        c <- CategoriesTable
        if c.id === pc.categoryId &&
        p.id === pc.productId &&
        c.leftValue >= cat.leftValue &&
        c.rightValue <= cat.rightValue &&
        {if (brandId > 0) p.brandId === brandId else ConstColumn.TRUE === ConstColumn.TRUE } &&
        {if (!search.isEmpty) p.title.like(s"%$search%") else ConstColumn.TRUE === ConstColumn.TRUE }
      } yield(p.id.count))
      val q2 = for {
        c <- CategoriesTable
        if (c.parentId === categoryId)
      } yield(c.id, c.title, subQuery(c) as "productCount")
      q2.list.map {
        case (a:Int,b:String, c:Int) => new CategoryListItem(a, b, c)
      }.filter(p => p.productCount > 0)
    }
  }
  def getBreadcrumbs(categoryId: Int, productId: Int, search:String): Seq[(Int, String)] = {
    database withSession {
      implicit val getCategoryItem = GetResult(r => Tuple2[Int, String](r.<<, r.<<))
      val category = get(categoryId)
      val query = Q.queryNA[(Int, String)](
        s"""
         select
            c.categoryId, c.title
         from
            categories c
         where
            c.leftValue ${if(productId > 0) "<=" else "<"} ${category.leftValue} and
            c.rightValue ${if(productId > 0) ">=" else ">"} ${category.rightValue}
        """)
      query.list
    }
  }
}
