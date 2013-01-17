package dao.common

import models.Picture

trait ImageRepository {
  def getDefaultImage: Picture

  def get(imageId: Int): Picture

  def getProductImage(productId: Int, imageNumber: Int): Picture

}
