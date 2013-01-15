package dao.impl.fake

import models.Picture

class ImageRepository extends dao.common.ImageRepository{
  def get(productId: Int, imageNumber: Int, imageSize: String): Picture = {
     new Picture(1, "/productimages/1_0" + imageNumber + ".jpg", "jpg")
  }
}
