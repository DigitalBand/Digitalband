package dao.common

import models.PictureEntity

trait ImageRepository {
  def listByProductId(productId: Int): Seq[Int]

  def getDefaultImage: PictureEntity

  def get(imageId: Int): PictureEntity

  def getProductImage(productId: Int, imageNumber: Int): PictureEntity

}
