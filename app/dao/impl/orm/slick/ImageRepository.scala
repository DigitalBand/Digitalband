package dao.impl.orm.slick

import common.RepositoryBase
import models.{PictureEntity}
import common.Profile.driver.simple._
import Database.threadLocalSession
import tables.ImagesTable

class ImageRepository extends RepositoryBase with dao.common.ImageRepository {

  def getDefaultImage = new PictureEntity(0, "/default/noimage.png", "jpg")

  def get(imageId: Int): models.PictureEntity = {
    if (imageId > 0) {
      database withSession {
        val imageQuery = for {
          img <- ImagesTable if img.id === imageId
        } yield (img.id, img.path)
        imageQuery.first match {
          case (id: Int, path: String) => PictureEntity(id, path, "jpg")
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
