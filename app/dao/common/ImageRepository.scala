package dao.common

import models.Picture

trait ImageRepository {
  def get(productId: Int, imageNumber: String, imageSize: String): Picture

}
