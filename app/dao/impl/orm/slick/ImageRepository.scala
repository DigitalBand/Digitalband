package dao.impl.orm.slick

import common.RepositoryBase
import models.{ImageTable, Picture}
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

class ImageRepository extends RepositoryBase with dao.common.ImageRepository {

  def getDefaultImage = new Picture(0, "/default/noimage.png", "jpg")

  def get(imageId: Int): models.Picture = {
    if (imageId > 0) {
      database withSession {
        val imageQuery = for {
          img <- ImageTable if img.id === imageId
        } yield (img.id, img.path)
        imageQuery.first match {
          case (id: Int, path: String) => Picture(id, path, "jpg")
        }
      }
    }
    else {
      getDefaultImage
    }
  }

  def getProductImage(productId: Int, imageNumber: Int) = ???
  def listByProductId(productId: Int): Seq[Int] = ???
}
