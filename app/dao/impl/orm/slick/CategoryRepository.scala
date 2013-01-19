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
         image => image match {
           case (id: Int, title: String, imageId: Int) => CategoryEntity(id, title, imageId)
         }
       }
     }
  }
}
