package dao.impl.orm.slick

import common.RepositoryBase
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import models.CategoryEntity
import models.BrandEntity

class BrandRepository extends RepositoryBase with dao.common.BrandRepository {
  //TODO: Implement this
  def list(getCategory: => CategoryEntity, pageNumber: Int): Seq[BrandEntity] = {

    database withSession {
      val category = getCategory
      def getBrands(leftValue: Int, rightValue: Int) = sql"""
      select t.*, (select imageId from brand_images where brandId = t.brandId limit 1) as imageId
         from
          (select b.brandId, b.title, count(b.brandId) as productCount
            from
              products p,
              products_categories pc,
              categories c,
              brands b
            where
              p.productId = pc.productId and
              c.CategoryId = pc.categoryId and
              c.LeftValue >= #$leftValue and c.rightValue <= #$rightValue
              and b.brandId = p.brandId
          group by p.brandId) t
      order by productCount desc
        """.as[BrandEntity]
      getBrands(category.leftValue, category.rightValue).list
    }
  }

}
