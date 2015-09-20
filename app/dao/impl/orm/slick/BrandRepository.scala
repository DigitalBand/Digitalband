package dao.impl.orm.slick

import common.RepositoryBase

import slick.jdbc.{GetResult, StaticQuery => Q}
import slick.driver.JdbcDriver.api._
import models.{ListPage, CategoryEntity, BrandEntity}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class BrandRepository extends RepositoryBase with dao.common.BrandRepository {
  implicit val getBrandEntityResult = GetResult(r => new BrandEntity(r.<<, r.<<, r.<<, r.nextIntOption().getOrElse(0)))

  def get(id: Int): Future[Option[BrandEntity]] = usingDB {
    sql"""
        select
          b.id,
          b.title,
          0,
          bi.image_id
        from
          brands b
        left join brand_images bi on bi.brand_id = b.id
        where
          b.id = ${id};
      """.as[BrandEntity].headOption
  }

  def list(getCategory: => CategoryEntity,
           pageNumber: Int,
           pageSize: Int,
           search: String,
           inStock: Boolean = false,
           domain: String): Future[ListPage[BrandEntity]] = {
    val category = getCategory
    val brandsFuture = usingDB {
      val drop = pageSize * (pageNumber - 1)
      val search1 = s"%${search}%"
      sql"""
        select
          b.id,
          b.title,
          count(p.brand_id) productCount,
          (select image_id from brand_images where brand_id = p.brand_id limit 1) as image_id
        from
           products p,
           products_categories pc,
           categories c,
           brands b
        where
           p.id = pc.product_id and
           c.category_id = pc.category_id and
           c.left_value >= ${category.leftValue} and
           c.right_value <= ${category.rightValue} and
           b.id = p.brand_id and
           ((${inStock} = FALSE) or ((
                                  select sum(quantity) from stock_items si
                                  left join shops s on s.id = si.shop_id
                                  left join cities c on c.id = s.city_id
                                  where
                                    product_id = p.id and c.domain = ${domain}
                                ) > 0)) and
           (${search} = '' or p.title like ${search1})
        group by p.brand_id order by productCount desc limit $drop, $pageSize;
      """.as[BrandEntity]
    }
    for {
      brandsCount <- getBrandsTotalCount(getCategory, search)
      brands <- brandsFuture
    } yield new ListPage(pageNumber, brands, brandsCount)
  }

  private def getBrandsTotalCount(category: CategoryEntity, search: String) = usingDB {
    val search1 = s"%${search}%"
    sql"""
      select
        count(t.brand_id)
      from (
        select
          p.brand_id
        from
          products p,
          products_categories pc,
          categories c
        where
          p.id = pc.product_id and
          c.category_id = pc.category_id and
          c.left_value >= ${category.leftValue} and
          c.right_value <= ${category.rightValue} and
          p.brand_id is not null and
          (${search} = '' or p.title like ${search1})
        group by
          p.brand_id
      ) t
    """.as[Int].head
  }

  def getBrandId(name: String): Future[Int] = {
    val brandIdFuture = usingDB {
      sql"select brand.id from brands brand where brand.title = ${name} limit 1;".as[Int].headOption
    }
    val newBrandIdFuture = usingDB {
      sql"""
        insert into brands(title) values($name);
        select last_insert_id();
      """.as[Int].headOption
    }
    for {
      brandId <- brandIdFuture
      newBrandId <- newBrandIdFuture if brandId.isEmpty
    } yield brandId.orElse(newBrandId).get
  }
}
