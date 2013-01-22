package dao.impl.fake

import models.Picture

class ImageRepository extends dao.common.ImageRepository {
  def getProductImage(productId: Int, imageNumber: Int): Picture = new Picture(1, "/productimages/1_0" + imageNumber + ".jpg", "jpg")

  def get(imageId: Int): Picture = {
    if (imageId > 0)
      new Picture(imageId, "/other/" + imageId + ".jpg", "jpg")
    else
      getDefaultImage
  }

  def getDefaultImage: Picture = new Picture(0, "/default/noimage.png", "jpg")

  def listByProductId(productId: Int): Seq[Int] = ???
}
