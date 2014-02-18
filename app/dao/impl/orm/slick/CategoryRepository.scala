package dao.impl.orm.slick

import common.{Profile, RepositoryBase}

import Profile.driver.simple._
import Database.threadLocalSession
import models.{CategoryListItem, CategoryEntity}

import tables.{CategoryImagesTable, CategoriesTable}
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

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

  def list(categoryId: Int, brandId: Int, search: String): Seq[CategoryListItem] = database withSession {
    implicit val getres = GetResult(r => CategoryListItem(r.<<, r.<<, r.<<))
    sql"""
      select
        scat.`categoryId`,
        scat.`title`,
        (
          select
            count(prod.`productId`)
          from
            `products_categories` prod_cat,
            `products` prod,
            `categories` cat
          where
            (cat.`categoryId` = prod_cat.`categoryId`) and
            (prod.`productId` = prod_cat.`productId`) and
            (cat.`leftValue` >= scat.`leftValue`) and
            (cat.`rightValue` <= scat.`rightValue`) and
            ((${brandId} = 0) or (prod.brandId = ${brandId})) and
            ((${search} = '') or (prod.title like ${'%'+search+'%'}))
        ) as productCount
      from
        `categories` scat
      where
        scat.`parentCategoryId` = ${categoryId}
    """.as[CategoryListItem].list.filter(p => p.productCount > 0)
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
