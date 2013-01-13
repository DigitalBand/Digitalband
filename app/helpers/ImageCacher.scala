package helpers

import java.io.{FileInputStream, File}
import models.Picture
import java.awt.image.BufferedImage
import java.nio._
import file.Path

class ImageCacher(val appPath: String) {

  def isCached(picture: Picture): Boolean = {
    file.Files.exists(combine(appPath, List(picture.path)))
  }

  def combine(path1: String, path2: Seq[String]): Path = {
    val file1 = new File(path1)
    if (path2.tail.isEmpty)
      new File(file1, path2.head).toPath()
    else
      combine(new File(file1, path2.head).getPath, path2.tail)
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
