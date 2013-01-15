package helpers

import java.awt.{Dimension}

object ImageHelper {
  def getDimension(imageSize: String) = {
    val arr = imageSize.split("x").map(_.toInt)
    new Dimension(arr(0), arr(1))
  }
  def getImageNumber(imageNumber: String): Int = {
    imageNumber.split("[\\.]")(0).toInt
  }
}
