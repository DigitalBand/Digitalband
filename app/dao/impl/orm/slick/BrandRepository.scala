package dao.impl.orm.slick

import common.RepositoryBase

import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession

import slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import models.{ListPage, CategoryEntity, BrandEntity}

class BrandRepository extends RepositoryBase with dao.common.BrandRepository {
  implicit val getBrandEntityResult = GetResult(r => new BrandEntity(r.<<, r.<<, r.<<, r.<<))

  def get(id: Int): Option[BrandEntity] = {
    database withDynSession  {
      //implicit val getBrand = GetResult(r => new BrandEntity(r.<<, r.<<, r.<<, r.<<))
      sql"""
        select
          b.brandId,
          b.title,
          0,
          bi.imageId
        from
          brands b
        inner join brand_images bi on b.brandId = b.brandId
        where
          b.brandId = ${id};
      """.as[BrandEntity].firstOption
    }
  }

  def list(getCategory: => CategoryEntity, pageNumber: Int, pageSize: Int, search: String, inStock:Boolean = false): ListPage[BrandEntity] = {
    database withDynSession {
      val category = getCategory
      val drop = pageSize * (pageNumber - 1)
      val brands = Q.queryNA[BrandEntity]( s"""
          select
            b.brandId,
            b.title,
            count(p.brandId) productCount,
            (select imageId from brand_images where brandId = p.brandId limit 1) as imageId
          from
             products p,
             products_categories pc,
             categories c,
             brands b
          where
             p.productId = pc.productId and
             c.CategoryId = pc.categoryId and
             c.LeftValue >= ${category.leftValue} and
             c.rightValue <= ${category.rightValue} and
             b.brandId = p.brandId and
             ((${inStock} = FALSE) or p.isAvailable = ${inStock}) and
             ${if (search.isEmpty) "1=1" else "p.title like '%" + search + "%'"}
          group by p.brandId order by productCount desc limit $drop, $pageSize;
        """)
      val brandsCount = Q.queryNA[Int]( s"""
        select
          count(t.brandId)
        from (
              select
                p.brandId
              from
                products p,
                products_categories pc,
                categories c
              where
                p.productId = pc.productId and
                c.CategoryId = pc.categoryId and
                c.LeftValue >= ${category.leftValue} and
                c.rightValue <= ${category.rightValue} and
                p.brandId is not null and
                ${if (search.isEmpty) "1=1" else "p.title like '%" + search + "%'"}
              group by
                p.brandId
              ) t
      """)
      new ListPage(pageNumber, brands.list, brandsCount.first())
    }
  }

  def getBrandId(name: String): Int = database withDynSession {
    sql"select brandId from brands where title = ${name} limit 1;".as[Int].firstOption match {
      case Some(id) => {
        id
      }
      case _ => {
        sqlu"insert into brands(title) values($name)".execute
        sql"select last_insert_id();".as[Int].first
      }
    }
  }
}
