package helpers

import java.io.File
import java.awt.image.BufferedImage
import java.nio.file._
import java.awt.Dimension
import play.api.Play
import play.api.mvc.Results
import models.Picture
import play.api.Play.current

object ImageCacher {
  val appPath = Play.application.path.toString
  def CachedFile(imageId: Int, outputDimension: Dimension,
                 compressQuality: Float = 0.5f, crop: Boolean = false, preserveAlpha: Boolean = true)(picture: => Picture) = {
    Results.Ok.sendFile(getImage(imageId, picture, outputDimension, crop, preserveAlpha, compressQuality))
  }
  def getImage(imageId: Int, picture: => Picture, outputDimension: Dimension, crop: Boolean = false, preserveAlpha: Boolean = true, compressQuality: Float): File = {
    val fill = crop match {case true => "cropped" case false => "full"}
    val quality = (compressQuality * 100).toInt.toString
    val cachePath = Paths.get(appPath, "data", "images", "cache", outputDimension.width + "x" + outputDimension.height, quality, fill, imageId + ".jpg")
    if (Files.exists(cachePath)) {
      new File(cachePath.toString)
    } else {
      val originalFile = new File(Paths.get(appPath, "data", "images", "originals", picture.path).toString)
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
