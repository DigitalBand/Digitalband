package helpers

import java.io.File
import java.awt.image.BufferedImage
import java.nio.file._
import java.awt.Dimension
import play.api.Play
import play.api.mvc.Results
import models.Picture
import play.api.Play.current

class ImageCacher(val appPath: String, val quality: Float = 0.5f) {


}
object ImageCacher {
  val appPath = Play.application.path.toString
  def CachedFile(outputDimension: Dimension,
                 compressQuality: Float = 0.5f, crop: Boolean = false, preserveAlpha: Boolean = true)(picture: => Picture) = {
    Results.Ok.sendFile(getImage(picture.path, outputDimension, crop, preserveAlpha, compressQuality))
  }
  def getImage(picturePath: String, outputDimension: Dimension, crop: Boolean = false, preserveAlpha: Boolean = true, compressQuality: Float): File = {
    val cachePath = Paths.get(appPath, "data", "images", "cache", outputDimension.width + "x" + outputDimension.height, picturePath)
    if (Files.exists(cachePath)) {
      new File(cachePath.toString)
    } else {
      val originalFile = new File(Paths.get(appPath, "data", "images", "originals", picturePath).toString)
      val resizedImage: BufferedImage = ImageResizer.resize(originalFile, outputDimension, preserveAlpha, crop)
      cache(resizedImage, cachePath, compressQuality)
    }
  }

  private def cache(image: BufferedImage, cachePath: Path, quality: Float): File = {
    val outputFile = new File(cachePath.toString)
    if (!outputFile.getParentFile.exists)
      outputFile.getParentFile.mkdirs
    ImageHelper.write(image, outputFile, quality)
  }
}
