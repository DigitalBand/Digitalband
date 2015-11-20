package dao.impl.orm.slick

import common.RepositoryBase
import slick.driver.MySQLDriver.api._
import models.{CategoryListItem, CategoryEntity}
import slick.jdbc.GetResult
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CategoryRepository extends RepositoryBase with dao.common.CategoryRepository {

  def listWithPictures: Future[Seq[CategoryEntity]] = usingDB {
    implicit val getCategory = GetResult(r => CategoryEntity(r.<<, r.<<, r.<<))
    sql"""
      SELECT
        cat.category_id,
        cat.title,
        ci.image_id
      FROM
        categories cat
      INNER JOIN category_images ci ON ci.category_id = cat.category_id
    """.as[CategoryEntity]
  }

  def get(id: Int): Future[CategoryEntity] = usingDB {
    implicit val getCategory = GetResult(r => CategoryEntity(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      SELECT
        cat.category_id,
        cat.title,
        0,
        cat.left_value,
        cat.right_value,
        cat.category_id
      FROM
        categories cat
      WHERE
        cat.category_id = ${id};
    """.as[CategoryEntity].head
  }

  def list(categoryId: Int,
           brandId: Int,
           search: String,
           inStock: Boolean,
           domain: String): Future[Seq[CategoryListItem]] = usingDB {
    implicit val getres = GetResult(r => CategoryListItem(r.<<, r.<<, r.<<))
    sql"""
      SELECT
        scat.category_id,
        scat.title,
        (
          SELECT
            count(prod.id)
          FROM
            products_categories prod_cat,
            products prod,
            categories cat
          WHERE
            prod.archived = FALSE AND
            (cat.category_id = prod_cat.category_id) AND
            (prod.id = prod_cat.product_id) AND
            (cat.left_value >= scat.left_value) AND
            (cat.right_value <= scat.right_value) AND
            ((${brandId} = 0) OR (prod.brand_id = ${brandId})) AND
            ((${search} = '') OR (prod.title LIKE ${'%' + search + '%'})) AND
            ((${inStock} = FALSE) OR ((
                                    SELECT sum(quantity) FROM stock_items si
                                    LEFT JOIN shops s ON s.id = si.shop_id
                                    LEFT JOIN cities c ON c.id = s.city_id
                                    WHERE
                                      product_id = prod.id AND c.domain = ${domain}
                                  ) > 0))
        ) AS productCount
      FROM
        `categories` scat
      WHERE
        scat.parent_category_id = ${categoryId}
    """.as[CategoryListItem]
  }.map(categoryListItems => categoryListItems.filter(p => p.productCount > 0))

  def getBreadcrumbs(categoryId: Int, productId: Int, search: String): Future[Seq[(Int, String)]] = {
    def queryFuture(category: CategoryEntity) = usingDB {
      sql"""
        SELECT
          cat.category_id,
          cat.title
        FROM
          categories cat
        WHERE
          ((${productId} > 0 AND cat.left_value <= ${category.leftValue}) OR (${productId} = 0 AND cat.left_value < ${category.leftValue})) AND
          ((${productId} > 0 AND cat.right_value >= ${category.rightValue}) OR (${productId} = 0 AND cat.right_value > ${category.rightValue}))
      """.as[(Int, String)]
    }
    for {
      category <- get(categoryId)
      breadcrumbs <- queryFuture(category)
    } yield breadcrumbs
  }

  def listAll: Future[Seq[CategoryEntity]] = usingDB {
    implicit val getCategoryResult = GetResult(r => CategoryEntity(id = r.<<, title = r.<<, parentId = r.<<))
    sql"""
      SELECT
        c.category_id,
        c.title,
        c.parent_category_id
      FROM categories c
    """.as[CategoryEntity]
  }
}
