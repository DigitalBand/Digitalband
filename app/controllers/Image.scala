package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.ImageRepository
import helpers.ImageCacher._
import helpers.ImageHelper._
import helpers.{DataStore, ImageResizer}
import java.io.File
import java.nio.file.Paths
import DataStore._

class Image @Inject()(val imageRepository: ImageRepository) extends Controller {

  def get(imageNumber: String, quality: Int, imageSize: String, fill: String) = Action {
    cachedImage(getImageId(imageNumber), getDimension(imageSize), checkQuality(quality), isCropped(fill)) {
      val picture = imageRepository.get(getImageId(imageNumber))
      val sourceFile = new File(Paths.get(imagesPath, "originals", picture.path).toString)
      ImageResizer.resize(sourceFile, getDimension(imageSize), true, isCropped(fill))
    }.withHeaders(
      CONTENT_TYPE -> "image/png",
      CONTENT_DISPOSITION -> "inline",
      CACHE_CONTROL -> "public, max-age=86400"
    )
  }


}
