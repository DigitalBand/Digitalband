package dao.common

import models.Picture

trait ImageRepository {
  def get(productId: Int, imageNumber: Int, imageSize: String): Picture

}
