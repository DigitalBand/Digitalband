package dao.impl.orm.slick

import common.RepositoryBase
import slick.jdbc.GetResult
import slick.driver.MySQLDriver.api._
import wt.common.image.{ImageEntity, PictureEntity}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ImageRepository extends RepositoryBase with dao.common.ImageRepository {

  implicit val getImage = GetResult(r => PictureEntity(r.<<, r.<<, r.<<))

  def defaultImage = Future(new PictureEntity(0, "/default/noimage.png", "jpg"))

  def errorImage = new PictureEntity(0, "/default/error.jpg", "jpg")

  def get(imageId: Int): Future[PictureEntity] = {
    if (imageId > 0) {
      usingDB {
        sql"""
          SELECT
            img.image_id,
            img.file_path,
            img.md5
          FROM
            images img
          WHERE
            img.image_id = ${imageId};
        """.as[PictureEntity].headOption
      }.map {
        case Some(x) => PictureEntity(x.id, x.path, "jpg") //TODO: jpg always?
        case None => errorImage
      }
    }
    else {
      defaultImage
    }
  }

  def getProductImage(productId: Int, imageNumber: Int) = ???

  def listByProductId(productId: Int): Future[Seq[Int]] = usingDB {
    sql"""
      SELECT
        pi.image_id
      FROM
        product_images pi
      WHERE
        pi.product_id = ${productId};
    """.as[Int]
  }

  def getByMd5(md5: String): Future[Option[PictureEntity]] = usingDB {
    sql"select image_id, file_path, md5 from images where md5 = $md5".as[PictureEntity].headOption
  }

  def create(img: ImageEntity): Future[Int] = {
    val insertFuture = usingDB {
      returningId(sql"""
        INSERT INTO images(file_path, md5) VALUES(${img.path}, ${img.md5})
      """.as[Int].head)
    }
    for {
      image <- getByMd5(img.md5)
      imageId <- insertFuture if image.nonEmpty
    } yield image match {
      case Some(i) => i.id
      case _ => imageId
    }
  }

  def remove(imageId: Int) = usingDB {
    sql"DELETE FROM images WHERE image_id = ${imageId}".as[Int].head
  }
}
