package dao.common


import wt.common.image.{ImageEntity, PictureEntity}

import scala.concurrent.Future

trait ImageRepository {
  def remove(imageId: Int): Future[Int]

  def create(img: ImageEntity): Future[Int]

  def listByProductId(productId: Int): Future[Seq[Int]]

  def defaultImage: Future[PictureEntity]

  def get(imageId: Int): Future[PictureEntity]

  def getProductImage(productId: Int, imageNumber: Int): Future[PictureEntity]

}
