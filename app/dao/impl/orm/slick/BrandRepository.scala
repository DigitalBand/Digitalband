package dao.impl.orm.slick

import common.RepositoryBase
import scala.slick.session.Database
import Database.threadLocalSession
import slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import models.{ListPage, CategoryEntity, BrandEntity}

class BrandRepository extends RepositoryBase with dao.common.BrandRepository {
  implicit val getBrandEntityResult = GetResult(r => BrandEntity(r.<<, r.<<, r.<<, r.<<))
  //TODO: rewrite for slick
  def list(getCategory: => CategoryEntity, pageNumber: Int, pageSize: Int): ListPage[BrandEntity] = {
    database withSession {
      val category = getCategory
      def getBrands(leftValue: Int, rightValue: Int, drop: Int, take: Int) = sql"""
          select b.brandId, b.title, count(p.brandId) productCount,
            (select imageId from brand_images where brandId = p.brandId limit 1) as imageId
                from
                  products p,
                  products_categories pc,
                  categories c,
                  brands b
                where
                  p.productId = pc.productId and
                  c.CategoryId = pc.categoryId and
                  c.LeftValue >= $leftValue and c.rightValue <= $rightValue
                  and b.brandId = p.brandId
              group by p.brandId order by productCount desc limit $drop, $take;
        """.as[BrandEntity]
      def getBrandsCount(leftValue: Int, rightValue: Int) = sql"""
        select count(t.brandId) from (
              select p.brandId
                from
                  products p,
                  products_categories pc,
                  categories c
                where
                  p.productId = pc.productId and
                  c.CategoryId = pc.categoryId and
                  c.LeftValue >= $leftValue and c.rightValue <= $rightValue
					and p.brandId is not null
              group by p.brandId) t
      """.as[Int]
      val brands = getBrands(category.leftValue, category.rightValue, pageSize*(pageNumber-1), pageSize).list
      val brandsCount = getBrandsCount(category.leftValue, category.rightValue).first()
      new ListPage(pageNumber, brands, brandsCount)
    }
  }

}
