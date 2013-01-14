package dao.impl.fake

import models.Picture

class ImageRepository extends dao.common.ImageRepository{
  def get(productId: Int, imageNumber: String, imageSize: String): Picture = {
     new Picture(1, "/productimages/1_01.jpg", "jpg")
  }
}
