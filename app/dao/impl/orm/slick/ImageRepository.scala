package dao.impl.orm.slick

import common.RepositoryBase
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import wt.common.image.{ImageEntity, PictureEntity}

class ImageRepository extends RepositoryBase with dao.common.ImageRepository {

  implicit val getImage = GetResult(r => PictureEntity(r.<<, r.<<, r.<<))

  def defaultImage = new PictureEntity(0, "/default/noimage.png", "jpg")

  def errorImage = new PictureEntity(0, "/default/error.jpg", "jpg")

  def get(imageId: Int): PictureEntity = {
    if (imageId > 0) {
      database withDynSession {

        sql"""
          select
            img.image_id,
            img.filePath,
            img.md5
          from
            images img
          where
            img.image_id = ${imageId};
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

  def listByProductId(productId: Int): Seq[Int] = database withDynSession {
    sql"""
      select
        pi.imageId
      from
        product_images pi
      where
        pi.productId = ${productId};
    """.as[Int].list
  }

  def getByMd5(md5: String): Option[PictureEntity] = database withDynSession {
    sql"select image_id, filePath, md5 from images where md5 = $md5".as[PictureEntity].firstOption
  }

  def create(img: ImageEntity): Int = database withDynSession {
    getByMd5(img.md5) match {
      case Some(i) => i.id
      case _ => {
        sqlu"insert into images(filePath, md5) values(${img.path}, ${img.md5})".execute()
        sql"select last_insert_id();".as[Int].first
      }
    }
  }

  def remove(imageId: Int) = database withDynSession  {
    sqlu"delete from images where image_id = ${imageId}".execute
  }
}
