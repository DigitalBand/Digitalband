package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.ImageRepository
import wt.common.image.{ImageHelper, ImageCacher}
import java.io.File
import java.nio.file.Paths
import wt.common.DataStore
import play.api.Play
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class Image @Inject()(val imageRepository: ImageRepository) extends Controller {
  //Paths.get(System.getProperty("user.home"), f.configuration.getString("data.root"))
  lazy val dataStore = new DataStore(Paths.get(System.getProperty("user.home"), Play.application.configuration.getString("data.root").get).toString)
  val imageCacher = ImageCacher(dataStore, None)
  val imageHelper = ImageHelper(dataStore)

  def get(imageNumber: String, quality: Int, imageSize: String, fill: String) = Action.async {
    import imageCacher._
    import imageHelper._
    val imageId = _getImageId(imageNumber)
    getSourceFile(imageId).map { file =>
      cachedImage(file, imageId, getDimension(imageSize), checkQuality(quality), isCropped(fill), preserveAlpha = false) {
        file =>
          Ok.sendFile(file)
      }.withHeaders(
          CONTENT_TYPE -> "image/jpeg",
          CONTENT_DISPOSITION -> "inline",
          CACHE_CONTROL -> "public, max-age=86400"
        )
    }
  }


  def _getImageId(imgNumber: String) = {
    imgNumber.split('.').head.toInt
  }
  def getSourceFile(imageId: Int): Future[File] = imageRepository.get(imageId).map {
    image =>
      new File(Paths.get(dataStore.imageOriginalsPath, image.path).toString)
  }

}
