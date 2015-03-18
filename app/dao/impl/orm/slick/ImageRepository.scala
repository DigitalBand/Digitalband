package dao.impl.orm.slick

import common.RepositoryBase
import slick.driver.JdbcDriver.backend.Database
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
          SELECT
            img.image_id,
            img.file_path,
            img.md5
          FROM
            images img
          WHERE
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
      SELECT
        pi.image_id
      FROM
        product_images pi
      WHERE
        pi.product_id = ${productId};
    """.as[Int].list
  }

  def getByMd5(md5: String): Option[PictureEntity] = database withDynSession {
    sql"select image_id, file_path, md5 from images where md5 = $md5".as[PictureEntity].firstOption
  }

  def create(img: ImageEntity): Int = database withDynSession {
    getByMd5(img.md5) match {
      case Some(i) => i.id
      case _ => {
        sqlu"INSERT INTO images(file_path, md5) VALUES(${img.path}, ${img.md5})".execute
        sql"SELECT last_insert_id();".as[Int].first
      }
    }
  }

  def remove(imageId: Int) = database withDynSession {
    sqlu"DELETE FROM images WHERE image_id = ${imageId}".execute
  }
}
