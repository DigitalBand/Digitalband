package dao.impl.orm.slick

import common.RepositoryBase
import models.{ImageTable, Picture}
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

class ImageRepository extends RepositoryBase with dao.common.ImageRepository {

  def getDefaultImage = ???

  def get(imageId: Int): models.Picture = {
    database withSession {
      val imageQuery = for {
        img <- ImageTable if img.id === imageId
      } yield (img.id, img.path)
      imageQuery.first match {
        case (id: Int, path: String) => Picture(id, path, "jpg")
      }
    }

  }

  def getProductImage(productId: Int, imageNumber: Int) = ???
}
