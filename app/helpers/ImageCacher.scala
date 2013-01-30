package helpers

import java.io.File
import java.awt.image.BufferedImage
import java.nio.file._
import java.awt.Dimension
import play.api.Play
import play.api.mvc.Results
import models.PictureEntity
import play.api.Play.current

object ImageCacher {
  val appPath = Play.application.path.toString
  val dataPath = Paths.get(appPath, "data").toString
  val imagesPath = Paths.get(dataPath, "images").toString

  def sendCachedImage(imageId: Int, outputDimension: Dimension, compressQuality: Float, crop: Boolean, preserveAlpha: Boolean = true)(picture: => PictureEntity) = {
    Results.Ok.sendFile(getImage(imageId, picture, outputDimension, crop, preserveAlpha, compressQuality))
  }
  def getImage(imageId: Int, picture: => PictureEntity, outputDimension: Dimension, crop: Boolean = false, preserveAlpha: Boolean = true, compressQuality: Float): File = {
    val fill = crop match {case true => "cropped" case false => "full"}
    val quality = (compressQuality * 100).toInt.toString
    val cachePath = Paths.get(imagesPath, "cache", outputDimension.width + "x" + outputDimension.height, quality, fill, imageId + ".jpg")
    if (Files.exists(cachePath)) {
      new File(cachePath.toString)
    } else {
      val resizedImage: BufferedImage = ImageResizer.resize(
        new File(Paths.get(imagesPath, "originals", picture.path).toString),
        new File(Paths.get(imagesPath, "originals", "default", "error.jpg").toString),
        outputDimension,
        preserveAlpha,
        crop)
      cache(resizedImage, cachePath.toString, compressQuality)
    }
  }

  private def cache(image: BufferedImage, cachePath: String, quality: Float): File = {
    val outputFile = new File(cachePath)
    if (!outputFile.getParentFile.exists)
      outputFile.getParentFile.mkdirs
    ImageHelper.write(image, outputFile, quality)
  }
}
