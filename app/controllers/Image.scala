package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.ImageRepository
import helpers.ImageCacher
import helpers.ImageHelper.{getDimension, getImageNumber}

class Image @Inject()(val imageRepository: ImageRepository) extends Controller {
  def productImage(productId: Int, imageNumber: String, imageSize: String) = Action {
    ImageCacher.CachedFile(getDimension(imageSize), 0.9f) {
      imageRepository.getProductImage(productId, getImageNumber(imageNumber))
    }
  }


  def categoryImage(imageName: String) = Action {
    val args = imageName.split("-")
    val imageSize: String = args(1).split("[\\.]")(0)
    val imageId: Int = args(0).toInt
    ImageCacher.CachedFile(getDimension(imageSize), 0.5f, crop = true){
      imageRepository.get(imageId)
    }
  }
}
