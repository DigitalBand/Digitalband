package dao.impl.orm.slick

import common.{Profile, RepositoryBase}
import Profile.driver.simple._
import Database.threadLocalSession

import slick.jdbc.{GetResult, StaticQuery => Q}

import models.{CategoryListItem, CategoryEntity}
import tables.{CategoryImagesTable, CategoriesTable}

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

  //TODO: convert to slick
  //TODO: implement search
  def list(categoryId: Int, brandId: Int, search:String): Seq[CategoryListItem] = {
    database withSession {
      implicit val getCategoryItem = GetResult(r => CategoryListItem(r.<<, r.<<, r.<<))
      val query = Q.queryNA[CategoryListItem](s"""
          select
            cat.categoryId,
            cat.title,
            (
              select
                count(product.productId)
              from
                products product
                right join products_categories productCategory on productCategory.productId = product.productId
                right join categories c on c.categoryId = productCategory.categoryId
              where
                c.leftValue >= cat.leftValue and
                c.rightValue <= cat.rightValue and
                ${brandId match {case 0 => "1=1" case _ => "product.brandId="+brandId}}
            ) as productCount
          from
            categories cat
          where
            cat.parentCategoryId = $categoryId;
                """)
      play.api.Logger.info(query.getStatement)
      query.list.filter(p => p.productCount > 0)
    }
  }
  def getBreadcrumbs(categoryId: Int, productId: Int): Seq[(Int, String)] = {
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
