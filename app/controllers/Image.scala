package controllers

import play.api.mvc.{Action, Controller}
import com.google.inject.Inject
import dao.common.ImageRepository
import models.Picture
import helpers.ImageCacher
import play.api.Play.current
import play.api.Play
import helpers.ImageHelper.getRectangle

class Image @Inject()(val imageRepository: ImageRepository) extends Controller {
  def get(productId: Int, imageNumber: String, imageSize: String) = Action {
    val picture: Picture = imageRepository.get(productId, imageNumber, imageSize)
    Ok.sendFile(new ImageCacher(Play.application.path.getPath).getImage(picture, getRectangle(imageSize)))
  }

}
