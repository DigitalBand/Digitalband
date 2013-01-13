package helpers

import java.io.{FileInputStream, File}
import models.Picture
import java.awt.image.BufferedImage
import java.nio.file._

class ImageCacher(val appPath: String) {

  def isCached(picture: Picture): Boolean = {
    Files.exists(Paths.get(appPath, picture.path))
  }

  def getFromCache(picture: Picture): File = ???

  def cache(image: BufferedImage): File = ???

  def getImage(picture: Picture): File = {
    if (isCached(picture))  {
      getFromCache(picture)
    } else {
      val file = getOriginal(picture)
      val image = ImageResizer.resize(new FileInputStream(file), 100, 100)
      cache(image)
    }
  }
  private def getOriginal(picture: Picture): File = {
    new File("/Users/Tim/Pictures/IMG_6376.JPG")
  }
}
