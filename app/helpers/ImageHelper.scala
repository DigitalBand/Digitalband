package helpers

import java.awt.{Toolkit, Color, Image, Dimension}
import java.awt.image.{FilteredImageSource, RGBImageFilter, BufferedImage}
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
  def imageType(image: BufferedImage) = ImageIO.getWriterFormatNames()(image.getType)
  //TODO: Implement
  def save(imageUrl: String)(insertImage: ImageEntity => Int): Int = {
    val url = new URL(imageUrl)
    val image = ImageIO.read(url)

    val os = new ByteArrayOutputStream()
    val bais = new ByteArrayInputStream(os.toByteArray())
    val md5 = getMd5(bais)
    ImageIO.write(image, imageType(image), os)


    val name = md5 + s".${imageType(image)}"
    val relativePath = Paths.get("productimages", name).toString
    val fileName = Paths.get(DataStore.imageOriginalsPath, relativePath).toString
    closable(new FileImageOutputStream(new File(fileName))) {
      fileStream =>
        ImageIO.write(image, imageType(image), fileStream)
    }
    insertImage(new ImageEntity(relativePath, md5))
  }
  def imageToBufferedImage(image:Image):BufferedImage= {
    val bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB)
    disposable(bufferedImage.createGraphics()){ g2=>
      g2.drawImage(image, 0, 0, null)
      bufferedImage
    }
  }
  def makeColorTransparent(im: BufferedImage, color: Color): Image = {
      val filter = new RGBImageFilter() {
      // the color we are looking for... Alpha bits are set to opaque
      val markerRGB = /*color.getRGB() | */0xFF000000
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
    if (imageType(image).toLowerCase() == "png" || imageType(image).toLowerCase() == "bmp"){
      val img = makeColorTransparent(image, new Color(image.getRGB(0,0)))
      ImageIO.write(imageToBufferedImage(img), "png", outputFile)
      outputFile
    }
    else {
      disposable(ImageIO.getImageWritersByFormatName("jpeg").next()) {
        jpegWriter =>
          val param: ImageWriteParam = jpegWriter.getDefaultWriteParam()
          param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
          param.setCompressionQuality(quality)
          closable(new FileImageOutputStream(outputFile)) {
            out =>
              jpegWriter.setOutput(out)
              jpegWriter.write(null, new IIOImage(image, null, null), param)
              outputFile
          }
      }
    }
  }
}
