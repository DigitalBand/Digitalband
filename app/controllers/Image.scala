package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.ImageRepository
import wt.common.image.{ImageHelper, ImageCacher, ImageResizer}
import java.io.File
import java.nio.file.Paths
import wt.common.DataStore
import play.api.Play


class Image @Inject()(val imageRepository: ImageRepository) extends Controller {
  lazy val dataStore = new DataStore(Play.maybeApplication.flatMap(_.configuration.getString("data.root")) getOrElse (System.getProperty("user.home")))
  val imageCacher = ImageCacher(dataStore, None)
  val imageHelper = ImageHelper(dataStore)
  val imageResizer = ImageResizer(None)

  def get(imageNumber: String, quality: Int, imageSize: String, fill: String) = Action {
    import imageCacher._
    import imageHelper._
    val imageId = _getImageId(imageNumber)
    cachedImage(getSourceFile(imageId), imageId, getDimension(imageSize), checkQuality(quality), isCropped(fill)) {
      imageData =>
        Ok(imageData)
    }.withHeaders(
        CONTENT_TYPE -> "image/jpg",
        CONTENT_DISPOSITION -> "inline",
        CACHE_CONTROL -> "public, max-age=86400"
      )
  }


  def _getImageId(imgNumber: String) = {
    imgNumber.split('.').head.toInt
  }
  def getSourceFile(imageId: Int) = new File(Paths.get(dataStore.imageOriginalsPath, imageRepository.get(imageId).path).toString)
}
