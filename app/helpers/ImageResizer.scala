package helpers

import java.awt.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.AlphaComposite

object ImageResizer {

  def resize(is:java.io.InputStream, maxWidth:Int, maxHeight:Int):BufferedImage = {
    val originalImage:BufferedImage = ImageIO.read(is)

    val height = originalImage.getHeight
    val width = originalImage.getWidth

    if (width <= maxWidth && height <= maxHeight)
      originalImage
    else {
      var scaledWidth:Int = width
      var scaledHeight:Int = height
      val ratio:Double = width/height
      if (scaledWidth > maxWidth){
        scaledWidth = maxWidth
        scaledHeight = (scaledWidth.doubleValue/ratio).intValue
      }
      if (scaledHeight > maxHeight){
        scaledHeight = maxHeight
        scaledWidth = (scaledHeight.doubleValue*ratio).intValue
      }
      val scaledBI = new BufferedImage(scaledWidth, scaledHeight,  BufferedImage.TYPE_INT_RGB)
      val g = scaledBI.createGraphics
      g.setComposite(AlphaComposite.Src)
      g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
      g.dispose
      scaledBI
    }
  }
}
