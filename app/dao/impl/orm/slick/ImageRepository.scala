package dao.impl.orm.slick

import common.RepositoryBase

import common.Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import wt.common.image.{ImageEntity, PictureEntity}

class ImageRepository extends RepositoryBase with dao.common.ImageRepository {

  implicit val getImage = GetResult(r => PictureEntity(r.<<, r.<<, r.<<))

  def defaultImage = new PictureEntity(0, "/default/noimage.png", "jpg")

  def errorImage = new PictureEntity(0, "/default/error.jpg", "jpg")

  def get(imageId: Int): PictureEntity = {
    if (imageId > 0) {
      database withSession {

        sql"""
          select
            img.imageId,
            img.filePath,
            img.md5
          from
            images img
          where
            img.imageId = ${imageId};
        """.as[PictureEntity].firstOption match {
          case Some(x) => PictureEntity(x.id, x.path, "jpg") //TODO: jpg always?
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
    sql"""
      select
        pi.imageId
      from
        product_images pi
      where
        pi.productId = ${productId};
    """.as[Int].list
  }

  def getByMd5(md5: String): Option[PictureEntity] = database withSession {
    sql"select imageId, filePath, md5 from images where md5 = $md5".as[PictureEntity].firstOption
  }

  def create(img: ImageEntity): Int = database withSession {
    getByMd5(img.md5) match {
      case Some(i) => i.id
      case _ => {
        sqlu"insert into images(filePath, md5) values(${img.path}, ${img.md5})".execute()
        sql"select last_insert_id();".as[Int].first
      }
    }
  }

  def remove(imageId: Int) = database withSession {
    sqlu"delete from images where imageId = $imageId".execute
  }
}
