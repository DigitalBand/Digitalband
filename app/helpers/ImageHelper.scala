package helpers

import java.awt.{Toolkit, Image, Dimension}
import java.awt.image.{FilteredImageSource, RGBImageFilter, BufferedImage}
import java.io._
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import javax.imageio.stream.{ImageOutputStream}
import play.api.libs.Files.TemporaryFile
import java.nio.file.Paths
import models.ImageEntity
import java.net.URL
import play.api.mvc.MultipartFormData.FilePart
import org.apache.commons.codec.digest.DigestUtils.md5Hex
import java.util.UUID

object ImageHelper {

  def deleteImage(relativePath: String) = {
    Paths.get(DataStore.imageOriginalsPath, relativePath).toFile.delete()
  }


  def imageType(file: File) = {
    ImageIO.getImageReaders(ImageIO.createImageInputStream(file)).next.getFormatName
  }

  def saveToTempFolder(imageUrl: String) = {
    val file = Paths.get(DataStore.imagesPath, "temp", UUID.randomUUID().toString + ".jpg").toFile
    org.apache.commons.io.FileUtils.copyURLToFile(new URL(imageUrl), file)
    file
  }

  def save(imageUrl: String): Option[ImageEntity] = {
    try {
      val file = saveToTempFolder(imageUrl)
      val imageEntity = getImageEntity(md5Hex(new FileInputStream(file)), imageType(file).toLowerCase)
      val fileName = Paths.get(DataStore.imageOriginalsPath, imageEntity.path).toString
      org.apache.commons.io.FileUtils.copyFile(file, new File(fileName))
      Some(imageEntity)
    } catch {
      case e: IOException => None
    }
  }

  def getImageEntity(md5: String, imageExtension: String) = {
    val name = md5 + s".${imageExtension}"
    val relativePath = Paths.get("productimages", name).toString
    new ImageEntity(relativePath, md5)
  }

  def imageToBufferedImage(image: Image): BufferedImage = {
    val bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB)
    disposable(bufferedImage.createGraphics()) {
      g2 =>
        g2.drawImage(image, 0, 0, null)
        bufferedImage
    }
  }

  def makeColorTransparent(im: BufferedImage): Image = {
    val filter = new RGBImageFilter() {
      val markerRGB = 0xFF000000

      def filterRGB(x: Int, y: Int, rgb: Int): Int = {
        if ((rgb | 0xFF000000) == markerRGB) {
          return 0x00FFFFFF & rgb
        } else {
          rgb
        }
      }
    }
    val ip = new FilteredImageSource(im.getSource(), filter)
    Toolkit.getDefaultToolkit().createImage(ip)
  }

  def save(picture: FilePart[TemporaryFile])(insertImage: ImageEntity => Unit) = {
    if (!picture.filename.isEmpty) {
      val imageEntity = getImageEntity(md5Hex(new FileInputStream(picture.ref.file)), imageType(picture.ref.file))
      val fileName = Paths.get(DataStore.imageOriginalsPath, imageEntity.path).toString
      picture.ref.moveTo(new File(fileName), true)
      insertImage(imageEntity)
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

  def makeTransparent(image: BufferedImage): BufferedImage = {
    val img = makeColorTransparent(image)
    imageToBufferedImage(img)
  }

  def write(image: BufferedImage, outputFile: ImageOutputStream, quality: Float): ImageOutputStream = {
    //TODO: Fix the problem with black background instead of transparent
    disposable(ImageIO.getImageWritersByFormatName("jpeg").next()) {
      jpegWriter =>
        val param: ImageWriteParam = jpegWriter.getDefaultWriteParam()
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
        param.setCompressionQuality(quality)
        closable(outputFile) {
          out =>
            jpegWriter.setOutput(out)
            jpegWriter.write(null, new IIOImage(image, null, null), param)
            outputFile
        }
    }
  }
}
