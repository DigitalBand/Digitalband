package helpers

import java.awt._
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.InputStream

class ImageResizer {
  //copied from java code TODO: improve for scala
  private def resize(is: InputStream, outputSize: Rectangle, preserveAlpha: Boolean, crop: Boolean) = {
    val sourceImage: BufferedImage = ImageIO.read(is)
    val sourceWidth = sourceImage.getWidth(null)
    val sourceHeight = sourceImage.getHeight(null)
    val widthScale = outputSize.width.toDouble / sourceWidth.toDouble
    val heightScale = outputSize.height.toDouble / sourceHeight.toDouble
    var scale = 0.0
    if (crop)
      scale = Math.max(widthScale, heightScale)
    else
      scale = Math.min(widthScale, heightScale)
    var imageType = BufferedImage.TYPE_INT_ARGB
    if (preserveAlpha)
      imageType = BufferedImage.TYPE_INT_RGB
    var canvas: Graphics2D = null
    try {
      val outputImage = new BufferedImage(outputSize.width, outputSize.height, imageType)
      canvas = outputImage.createGraphics()
      if (preserveAlpha) {
        canvas.setComposite(AlphaComposite.Src)
      }
      //set background
      canvas.setPaint(Color.white)
      canvas.fillRect(0, 0, outputSize.width, outputSize.height)
      val scaledWidth = (sourceWidth * scale).toInt
      val scaledHeight = (sourceHeight * scale).toInt
      val scaledImage = sourceImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH)
      val positionLeft = (outputSize.width - scaledWidth) / 2
      val positionTop = (outputSize.height - scaledHeight) / 2

      canvas.drawImage(scaledImage, positionLeft, positionTop, scaledWidth, scaledHeight, null)
      outputImage
    } finally {
      if (canvas != null) {
        canvas.dispose()
      }
    }
  }

  def resize(is: java.io.InputStream, maxSize: Rectangle): BufferedImage =
    resize(is, maxSize, preserveAlpha = true, crop = false)
}
