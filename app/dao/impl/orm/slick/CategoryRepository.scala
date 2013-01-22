package dao.impl.orm.slick

import common.RepositoryBase
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession
import models.{CategoryEntity, CategoryImages, CategoryTable}

class CategoryRepository extends RepositoryBase with dao.common.CategoryRepository {

  def getListWithPictures: Seq[CategoryEntity] = {
     database withSession {
       val categories = for {
         c <- CategoryTable
         ci <- CategoryImages if c.id === ci.categoryId
       } yield (c.id, c.title, ci.imageId)
       categories.list.map {
           case (id: Int, title: String, imageId: Int) => CategoryEntity(id, title, imageId)
       }
     }
  }

  def get(id: Int): CategoryEntity = {
    database withSession {
      val categoryQuery = CategoryTable.filter(_.id === id).map(c => c.id ~ c.title ~ c.leftValue ~ c.rightValue)
      categoryQuery.list.map {
       case (id: Int, title: String, leftValue: Int, rightValue: Int) => CategoryEntity(id, title, 0, leftValue, rightValue)
      }.head
    }
  }
}
