package dao.impl.fake

import models.PictureEntity

class ImageRepository extends dao.common.ImageRepository {
  def getProductImage(productId: Int, imageNumber: Int): PictureEntity = new PictureEntity(1, "/productimages/1_0" + imageNumber + ".jpg", "jpg")

  def get(imageId: Int): PictureEntity = {
    if (imageId > 0)
      new PictureEntity(imageId, "/other/" + imageId + ".jpg", "jpg")
    else
      defaultImage
  }

  def defaultImage: PictureEntity = new PictureEntity(0, "/default/noimage.png", "jpg")

  def listByProductId(productId: Int): Seq[Int] = ???
}
