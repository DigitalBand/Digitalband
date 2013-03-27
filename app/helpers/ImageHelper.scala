package helpers

import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io._
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import javax.imageio.stream.FileImageOutputStream
import play.api.libs.Files.TemporaryFile
import java.nio.file.Paths
import models.ImageEntity
import java.net.URL
import play.api.mvc.MultipartFormData.FilePart

object ImageHelper {

  def getMd5(f: File): String = getMd5(new FileInputStream(f))

  def getMd5(fis: java.io.InputStream): String = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis)

  def deleteImage(relativePath: String) = {
    Paths.get(DataStore.imageOriginalsPath, relativePath).toFile.delete()
  }

  //TODO: Implement
  def save(imageUrl: String)(insertImage: ImageEntity => Int): Int = {
    val url = new URL(imageUrl)
    val image = ImageIO.read(url)
    val md5 = closable(new ByteArrayOutputStream()) {
      os =>
        ImageIO.write(image, "jpg", os)
        getMd5(new ByteArrayInputStream(os.toByteArray()))
    }
    val name = md5 + ".jpg"
    val relativePath = Paths.get("productimages", name).toString
    val fileName = Paths.get(DataStore.imageOriginalsPath, relativePath).toString
    closable(new FileImageOutputStream(new File(fileName))) {
      fileStream =>
        ImageIO.write(image, "jpg", fileStream)
    }
    insertImage(new ImageEntity(relativePath, md5))
  }

  def save(picture: FilePart[TemporaryFile])(insertImage: ImageEntity => Int): Int = {
    if (!picture.filename.isEmpty) {
      val md5 = getMd5(picture.ref.file)
      val name = md5 + ".jpg"
      val relativePath = Paths.get("productimages", name).toString
      val fileName = Paths.get(DataStore.imageOriginalsPath, relativePath).toString
      picture.ref.moveTo(new File(fileName), true)
      insertImage(new ImageEntity(relativePath, md5))
    } else {
      0
    }
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
