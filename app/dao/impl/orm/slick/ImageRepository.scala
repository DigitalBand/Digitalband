package dao.impl.orm.slick

import common.RepositoryBase
import models.{PictureEntity}
import common.Profile.driver.simple._
import Database.threadLocalSession
import tables.{ProductImagesTable, ImagesTable}

class ImageRepository extends RepositoryBase with dao.common.ImageRepository {

  def getDefaultImage = new PictureEntity(0, "/default/noimage.png", "jpg")
  def getErrorImage = new PictureEntity(0, "/default/error.jpg", "jpg")
  def get(imageId: Int): models.PictureEntity = {
    if (imageId > 0) {
      database withSession {
        val imageQuery = for {
          img <- ImagesTable if img.id === imageId
        } yield (img.id, img.path)
        imageQuery.firstOption match {
          case Some(x) => PictureEntity(x._1, x._2, "jpg")
          case None => getErrorImage
        }
      }
    }
    else {
      getDefaultImage
    }
  }

  def getProductImage(productId: Int, imageNumber: Int) = ???
  def listByProductId(productId: Int): Seq[Int] =  database withSession {
    ProductImagesTable.filter(i => i.productId === productId).map(_.imageId).list
  }
}
