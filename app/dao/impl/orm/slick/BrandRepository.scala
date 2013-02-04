package dao.impl.orm.slick

import common.{Profile, RepositoryBase}
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import models.{ListPage, CategoryEntity, BrandEntity}
import tables.{BrandImagesTable, BrandsTable}

class BrandRepository extends RepositoryBase with dao.common.BrandRepository {
  implicit val getBrandEntityResult = GetResult(r => BrandEntity(r.<<, r.<<, r.<<, r.<<))
  def get(id: Int): Option[BrandEntity] = {
    database withSession {
      val query = for {
        (b, bi) <- BrandsTable leftJoin BrandImagesTable on (_.id === _.brandId)if b.id === id
      } yield (b.id, b.title, 0, bi.imageId.?)
      query.firstOption.map {
        case (id:Int, title:String, productCount:Int, imageId:Option[Int]) => BrandEntity(id, title, productCount, imageId.getOrElse(0))
      }
    }
  }
  //TODO: rewrite for slick
  //TODO: implement search
  def list(getCategory: => CategoryEntity, pageNumber: Int, pageSize: Int, search: String): ListPage[BrandEntity] = {
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
