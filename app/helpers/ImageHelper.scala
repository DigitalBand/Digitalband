package helpers

import java.awt.{Dimension}
import java.awt.image.BufferedImage
import java.io.{FileInputStream, File}
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import javax.imageio.stream.FileImageOutputStream
import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile
import java.nio.file.Paths
import models.ImageEntity
import java.security.DigestInputStream

object ImageHelper {

  import java.security.MessageDigest

  def getMd5(f: File) = {
    val fis = new FileInputStream(f)
    org.apache.commons.codec.digest.DigestUtils.md5Hex(fis)
  }

  def save(filePart: Option[FilePart[TemporaryFile]])(f: ImageEntity => Int): Int = {
    filePart.map {
      picture =>
        if (!picture.filename.isEmpty) {
          val md5 = getMd5(picture.ref.file)
          val name = md5 + ".jpg"
          val relativePath = Paths.get("productimages", name).toString
          val fileName = Paths.get(DataStore.imageOriginalsPath, relativePath).toString
          picture.ref.moveTo(new File(fileName), true)
          f(new ImageEntity(relativePath, md5))
        } else {
          0
        }
    }.getOrElse(0)
  }

  def getDimension(imageSize: String) = {
    val arr = imageSize.split("x").map(_.toInt)
    new Dimension(arr(0), arr(1))
  }

  def isCropped(fill: String) = {
    fill match {
      case "cropped" => true
      case "full" => false
    }
  }

  def checkQuality(quality: Int): Float = {
    if (quality < 20)
      0.2f
    else if (quality > 100)
      1.0f
    else
      (quality.toFloat / 100)
  }

  def getImageId(imageNumber: String): Int = {
    imageNumber.split("[\\.]")(0).toInt
  }

  def write(image: BufferedImage, outputFile: File, quality: Float): File = {
    disposable(ImageIO.getImageWritersByFormatName("jpeg").next()) {
      jpegWriter =>
        val param: ImageWriteParam = jpegWriter.getDefaultWriteParam()
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
        param.setCompressionQuality(quality)
        closable(new FileImageOutputStream(outputFile)) {
          out =>
            jpegWriter.setOutput(
              out)
            jpegWriter.write(null, new IIOImage(image, null, null), param)
            outputFile
        }
    }
  }
}
