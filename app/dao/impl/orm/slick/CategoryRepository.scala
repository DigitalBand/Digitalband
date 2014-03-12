package dao.impl.orm.slick

import common.RepositoryBase
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import models.{CategoryListItem, CategoryEntity}

import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

class CategoryRepository extends RepositoryBase with dao.common.CategoryRepository {


  def listWithPictures: Seq[CategoryEntity] = database withDynSession {
    implicit val getCategory = GetResult(r => CategoryEntity(r.<<, r.<<, r.<<))
    sql"""
      select
        cat.CategoryId,
        cat.title,
        ci.imageId
      from
        categories cat
      inner join category_images ci on ci.categoryId = cat.categoryid
    """.as[CategoryEntity].list
  }


  def get(id: Int): CategoryEntity = database withDynSession {
    implicit val getCategory = GetResult(r => CategoryEntity(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        cat.CategoryId,
        cat.title,
        0,
        cat.leftvalue,
        cat.rightvalue,
        cat.parentcategoryid
      from
        categories cat
      where
        cat.categoryid = ${id};
    """.as[CategoryEntity].first

  }

  def list(categoryId: Int, brandId: Int, search: String, inStock: Boolean): Seq[CategoryListItem] = database withDynSession {
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
            prod.archived = false and
            (cat.`categoryId` = prod_cat.`categoryId`) and
            (prod.`productId` = prod_cat.`productId`) and
            (cat.`leftValue` >= scat.`leftValue`) and
            (cat.`rightValue` <= scat.`rightValue`) and
            ((${brandId} = 0) or (prod.brand_id = ${brandId})) and
            ((${search} = '') or (prod.title like ${'%' + search + '%'})) and
            ((${inStock} = FALSE) or (prod.isAvailable = ${inStock}))
        ) as productCount
      from
        `categories` scat
      where
        scat.`parentCategoryId` = ${categoryId}
    """.as[CategoryListItem].list.filter(p => p.productCount > 0)
  }

  def getBreadcrumbs(categoryId: Int, productId: Int, search: String): Seq[(Int, String)] = {
    database withDynSession {
      val category = get(categoryId)
      sql"""
        select
          cat.CategoryId,
          cat.title
        from
          categories cat
        where
          ((${productId} > 0 and cat.leftValue <= ${category.leftValue}) or (${productId} = 0 and cat.leftValue < ${category.leftValue})) and
          ((${productId} > 0 and cat.rightValue >= ${category.rightValue}) or (${productId} = 0 and cat.rightValue > ${category.rightValue}))
      """.as[(Int, String)].list
    }
  }
}
