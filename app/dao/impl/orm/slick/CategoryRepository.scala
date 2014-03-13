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
        cat.category_id,
        cat.title,
        ci.image_id
      from
        categories cat
      inner join category_images ci on ci.category_id = cat.category_id
    """.as[CategoryEntity].list
  }


  def get(id: Int): CategoryEntity = database withDynSession {
    implicit val getCategory = GetResult(r => CategoryEntity(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        cat.category_id,
        cat.title,
        0,
        cat.left_value,
        cat.right_value,
        cat.category_id
      from
        categories cat
      where
        cat.category_id = ${id};
    """.as[CategoryEntity].first

  }

  def list(categoryId: Int, brandId: Int, search: String, inStock: Boolean): Seq[CategoryListItem] = database withDynSession {
    implicit val getres = GetResult(r => CategoryListItem(r.<<, r.<<, r.<<))
    sql"""
      select
        scat.category_id,
        scat.title,
        (
          select
            count(prod.id)
          from
            products_categories prod_cat,
            products prod,
            categories cat
          where
            prod.archived = false and
            (cat.category_id = prod_cat.categoryId) and
            (prod.id = prod_cat.productId) and
            (cat.left_value >= scat.left_value) and
            (cat.right_value <= scat.right_value) and
            ((${brandId} = 0) or (prod.brand_id = ${brandId})) and
            ((${search} = '') or (prod.title like ${'%' + search + '%'})) and
            ((${inStock} = FALSE) or (prod.isAvailable = ${inStock}))
        ) as productCount
      from
        `categories` scat
      where
        scat.parent_category_id = ${categoryId}
    """.as[CategoryListItem].list.filter(p => p.productCount > 0)
  }

  def getBreadcrumbs(categoryId: Int, productId: Int, search: String): Seq[(Int, String)] = {
    database withDynSession {
      val category = get(categoryId)
      sql"""
        select
          cat.category_id,
          cat.title
        from
          categories cat
        where
          ((${productId} > 0 and cat.left_value <= ${category.leftValue}) or (${productId} = 0 and cat.left_value < ${category.leftValue})) and
          ((${productId} > 0 and cat.right_value >= ${category.rightValue}) or (${productId} = 0 and cat.right_value > ${category.rightValue}))
      """.as[(Int, String)].list
    }
  }
}
