package helpers

import java.awt.{Color, Image, Graphics2D, AlphaComposite}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.InputStream

object ImageResizer {
  //copied from java code TODO: improve for scala
  def getResizedImage(is: InputStream, outputWidth: Int, outputHeight: Int, preserveAlpha: Boolean = true, crop: Boolean = false) = {
    val sourceImage:BufferedImage = ImageIO.read(is)
    val sourceWidth = sourceImage.getWidth(null)
    val sourceHeight = sourceImage.getHeight(null)
    val widthScale = outputWidth.toDouble / sourceWidth.toDouble
    val heightScale = outputHeight.toDouble / sourceHeight.toDouble
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
      val outputImage = new BufferedImage(outputWidth, outputHeight, imageType);
      canvas = outputImage.createGraphics();
      if (preserveAlpha) {
        canvas.setComposite(AlphaComposite.Src);
      }
      //set background
      canvas.setPaint(Color.white);
      canvas.fillRect(0, 0, outputWidth, outputHeight)
      val scaledWidth = (sourceWidth * scale).toInt
      val scaledHeight = (sourceHeight * scale).toInt
      val scaledImage = sourceImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
      val positionLeft = (outputWidth - scaledWidth) / 2;
      val positionTop = (outputHeight - scaledHeight) / 2;

      canvas.drawImage(scaledImage, positionLeft, positionTop, scaledWidth, scaledHeight, null);
      outputImage
    } finally {
      if (canvas != null) {
        canvas.dispose();
      }
    }
  }

  def resize(is:java.io.InputStream, maxWidth:Int, maxHeight:Int):BufferedImage = getResizedImage(is, maxWidth, maxHeight)
}
