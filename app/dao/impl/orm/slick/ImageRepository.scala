package dao.impl.orm.slick

import common.RepositoryBase
import models.{ImageEntity, PictureEntity}
import common.Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import tables.{ProductImagesTable, ImagesTable}

class ImageRepository extends RepositoryBase with dao.common.ImageRepository {

  def defaultImage = new PictureEntity(0, "/default/noimage.png", "jpg")

  def errorImage = new PictureEntity(0, "/default/error.jpg", "jpg")

  def get(imageId: Int): models.PictureEntity = {
    if (imageId > 0) {
      database withSession {
        val imageQuery = for {
          img <- ImagesTable if img.id === imageId
        } yield (img.id, img.path)
        imageQuery.firstOption match {
          case Some(x) => PictureEntity(x._1, x._2, "jpg")
          case None => errorImage
        }
      }
    }
    else {
      defaultImage
    }
  }

  def getProductImage(productId: Int, imageNumber: Int) = ???

  def listByProductId(productId: Int): Seq[Int] = database withSession {
    ProductImagesTable.filter(i => i.productId === productId).map(_.imageId).list
  }

  def create(img: ImageEntity): Int = database withSession {

    val query = sqlu"insert into images(filePath, md5) values(${img.path}, ${img.md5})"
    val statement = query.getStatement
      query.execute()
    sql"select last_insert_id();".as[Int].first
  }
}
