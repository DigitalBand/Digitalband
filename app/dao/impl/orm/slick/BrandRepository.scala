package dao.impl.orm.slick

import common.{Profile, RepositoryBase}
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import models.{ListPage, CategoryEntity, BrandEntity}
import tables.{BrandImagesTable, BrandsTable}

class BrandRepository extends RepositoryBase with dao.common.BrandRepository {
  implicit val getBrandEntityResult = GetResult(r => new BrandEntity(r.<<, r.<<, r.<<, r.<<))

  def get(id: Int): Option[BrandEntity] = {
    database withSession {
      val query = for {
        (b, bi) <- BrandsTable leftJoin BrandImagesTable on (_.id === _.brandId) if b.id === id
      } yield (b.id, b.title, 0, bi.imageId.?)
      query.firstOption.map {
        case (id: Int, title: String, productCount: Int, imageId: Option[Int]) => new BrandEntity(id, title, productCount, imageId.getOrElse(0))
      }
    }
  }

  def list(getCategory: => CategoryEntity, pageNumber: Int, pageSize: Int, search: String): ListPage[BrandEntity] = {
    database withSession {
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

  def getBrandId(name: String): Int = database withSession {
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
