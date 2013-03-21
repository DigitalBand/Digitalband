package helpers

import java.awt.{Dimension}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import javax.imageio.stream.FileImageOutputStream

object ImageHelper {
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
    disposable(ImageIO.getImageWritersByFormatName("jpeg").next()) { jpegWriter =>
      val param: ImageWriteParam = jpegWriter.getDefaultWriteParam()
      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
      param.setCompressionQuality(quality)
      closable(new FileImageOutputStream(outputFile)) {  out =>
        jpegWriter.setOutput(
          out)
        jpegWriter.write(null, new IIOImage(image, null, null), param)
        outputFile
      }
    }
  }
}
