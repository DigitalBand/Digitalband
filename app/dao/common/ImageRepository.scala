package dao.common

import models.{ImageEntity, PictureEntity}

trait ImageRepository {
  def create(img: ImageEntity): Int

  def listByProductId(productId: Int): Seq[Int]

  def defaultImage: PictureEntity

  def get(imageId: Int): PictureEntity

  def getProductImage(productId: Int, imageNumber: Int): PictureEntity

}
