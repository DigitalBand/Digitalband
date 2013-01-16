package controllers

import play.api.mvc.{Action, Controller}
import com.google.inject.Inject
import dao.common.ImageRepository
import models.Picture
import helpers.ImageCacher
import play.api.Play.current
import play.api.Play
import helpers.ImageHelper.{getDimension, getImageNumber}

class Image @Inject()(val imageRepository: ImageRepository) extends Controller {
  def productImage(productId: Int, imageNumber: String, imageSize: String) = Action {
    val picture: Picture = imageRepository.getProductImage(productId, getImageNumber(imageNumber))
    Ok.sendFile(new ImageCacher(Play.application.path.getPath).getImage(picture, getDimension(imageSize)))
  }

  def categoryImage(imageName: String) = Action {
    val args = imageName.split("-")
    val imageSize: String = args(1).split("[\\.]")(0)
    val imageId: Int = args(0).toInt
    val picture: Picture = imageRepository.get(imageId)
    Ok.sendFile(new ImageCacher(Play.application.path.getPath).getImage(picture, getDimension(imageSize), true))
  }
}
