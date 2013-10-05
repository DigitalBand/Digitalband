package helpers

import java.io._
import java.awt.image.BufferedImage
import java.nio.file._
import java.awt.Dimension
import play.api.mvc.Results
import DataStore._
import play.api.libs.iteratee.Enumerator
import javax.imageio.ImageIO
import javax.imageio.stream.{ImageOutputStream, FileImageOutputStream}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play
import helpers.closable

object DataStore {
  def appPath = {
    Play.maybeApplication.flatMap(_.configuration.getString("data.root")) getOrElse (System.getProperty("user.home"))
  }
  val dataPath = Paths.get(appPath, "data").toString
  val imagesPath = Paths.get(dataPath, "images").toString
  val imageOriginalsPath = Paths.get(imagesPath, "originals").toString
  val productOriginalImagePath = Paths.get(imageOriginalsPath, "productimages").toString

}

object ImageCacher {


  def cachedImage(imageId: Int, outputDimension: Dimension, compressQuality: Float, crop: Boolean, preserveAlpha: Boolean = true, cacheDisabled: Boolean = false)(resize: => BufferedImage) = {
    if (cacheDisabled) readImage(imageId, outputDimension, compressQuality, crop, preserveAlpha)(resize)
    else {
      val fill = crop match {
        case true => "cropped"
        case false => "full"
      }
      val quality = (compressQuality * 100).toInt.toString
      val cachePath = Paths.get(imagesPath, "cache", outputDimension.width + "x" + outputDimension.height, quality, fill, imageId + ".jpg")
      if (Files.exists(cachePath)) {
        val content = Enumerator.fromStream(new FileInputStream(new File(cachePath.toString)))
        Results.Ok(toByteArr(cachePath.toFile))
      } else {
        readImage(imageId, outputDimension, compressQuality, crop, preserveAlpha)(resize)
      }
    }
  }

  private def readImage(imageId: Int, outputDimension: Dimension, compressQuality: Float, crop: Boolean, preserveAlpha: Boolean = true)(resize: => BufferedImage) = {
    val fill = crop match {
      case true => "cropped"
      case false => "full"
    }
    val quality = (compressQuality * 100).toInt.toString
    val cachePath = Paths.get(imagesPath, "cache", outputDimension.width + "x" + outputDimension.height, quality, fill, imageId + ".jpg")
    val resizedImage: BufferedImage = resize
    cache(resizedImage, cachePath.toString, compressQuality)
    val content = Enumerator.fromFile(cachePath.toFile)
    Results.Ok(toByteArr(cachePath.toFile))
  }

  def toByteArr(file: File) = {
    closable(scala.io.Source.fromFile(file)(scala.io.Codec.ISO8859)) { source =>
      source.map(_.toByte).toArray
    }
  }
  def getStream(bi: BufferedImage) = {
    val baos = new ByteArrayOutputStream()
    ImageIO.write(bi, "png", baos)
    new ByteArrayInputStream(baos.toByteArray())
  }

  def convert(stream1: ImageOutputStream): InputStream = {
    new ByteArrayInputStream(stream1.asInstanceOf[ByteArrayOutputStream].toByteArray())
  }

  private def cache(image: BufferedImage, cachePath: String, quality: Float): ImageOutputStream = {
    val outputFile = new File(cachePath)
    if (!outputFile.getParentFile.exists)
      outputFile.getParentFile.mkdirs
    ImageHelper.write(image, new FileImageOutputStream(outputFile), quality)
  }
}
