package helpers

import java.awt._
import java.awt.image.BufferedImage
import javax.imageio.{IIOException, ImageIO}
import java.io.File

object ImageResizer {
  def resize(originalImageFile: File, errorFile: File, outputSize: Dimension, preserveAlpha: Boolean, crop: Boolean): BufferedImage = {
    val originalImage: BufferedImage = try ImageIO.read(originalImageFile) catch {
      case e:IIOException => ImageIO.read(errorFile)
    }
    val originalImageSize = getImageSize(originalImage)
    val scaledRectangle = getScaledRectangle(originalImageSize, outputSize, getRatio(crop, outputSize, originalImageSize))
    drawImage(originalImage, new BufferedImage(outputSize.width, outputSize.height, getImageType(preserveAlpha)), outputSize, scaledRectangle, preserveAlpha)
  }

  private def drawImage(sourceImage: BufferedImage, outputImage: BufferedImage, outputSize: Dimension, scaledRectangle: Rectangle, preserveAlpha: Boolean) = {
    using (outputImage.createGraphics()) { canvas =>
      val scaledImage = sourceImage.getScaledInstance(scaledRectangle.width, scaledRectangle.height, Image.SCALE_SMOOTH)
      if (preserveAlpha) canvas.setComposite(AlphaComposite.Src)
      canvas.setPaint(Color.white)
      canvas.fillRect(0, 0, outputSize.width, outputSize.height)
      canvas.drawImage(scaledImage, scaledRectangle.x, scaledRectangle.y, scaledRectangle.width, scaledRectangle.height, null)
      outputImage
    }
  }

  private def getScaledRectangle(sourceSize: Rectangle, outputSize: Dimension, ratio: Double) = {
    val scaledDimension = new Dimension((sourceSize.width * ratio).toInt, (sourceSize.height * ratio).toInt)
    val relativePoint = new Point((outputSize.width - scaledDimension.width) / 2, (outputSize.height - scaledDimension.height) / 2)
    new Rectangle(relativePoint, scaledDimension)
  }

  private def getImageType(preserveAlpha: Boolean) = preserveAlpha match {
    case true => BufferedImage.TYPE_INT_RGB
    case false => BufferedImage.TYPE_INT_RGB
  }

  private def getRatio(crop: Boolean, outputSize: Dimension, sourceSize: Rectangle): Double = {
    val widthRatio = outputSize.width.toDouble / sourceSize.width.toDouble
    val heightRatio = outputSize.height.toDouble / sourceSize.height.toDouble
    crop match {
      case true => Math.max(widthRatio, heightRatio)
      case false => Math.min(widthRatio, heightRatio)
    }
  }

  private def getImageSize(image: BufferedImage) = new Rectangle(image.getWidth(null), image.getHeight(null))
}
