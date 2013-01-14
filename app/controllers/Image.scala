package controllers

import play.api.mvc.{Action, Controller}
import com.google.inject.Inject
import dao.common.ImageRepository
import models.Picture
import helpers.ImageCacher
import play.api.Play.current
import play.api.Play

class Image @Inject()(val imageRepository: ImageRepository) extends Controller {
  def get(productId: Int, imageNumber: String, imageSize: String) = Action {
    val picture: Picture = imageRepository.get(productId, imageNumber, imageSize)
    val dimensions = imageSize.split("x").map(_.toInt)
    Ok.sendFile(new ImageCacher(Play.application.path.getPath).getImage(picture, dimensions(0), dimensions(1)))
  }
}
