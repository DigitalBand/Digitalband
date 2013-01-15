package helpers

import java.awt.Rectangle

object ImageHelper {
  def getRectangle(imageSize: String): Rectangle = {
    val arr = imageSize.split("x").map(_.toInt)
    new Rectangle(arr(0), arr(1))
  }
}
