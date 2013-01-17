package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.ImageRepository
import helpers.ImageCacher._
import helpers.ImageHelper._

class Image @Inject()(val imageRepository: ImageRepository) extends Controller {
  def get(imageNumber: String, quality: Int, imageSize: String, fill: String) = Action {
    val imageId: Int = getImageId(imageNumber)
    sendCachedImage(imageId, getDimension(imageSize), checkQuality(quality), isCropped(fill)) {
      if (imageId > 0)
        imageRepository.get(imageId)
      else
        imageRepository.getDefaultImage
    }
  }
}
