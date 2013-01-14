package helpers

import java.io.{FileInputStream, File}
import models.Picture
import java.awt.image.BufferedImage
import java.nio.file._
import javax.imageio.ImageIO

class ImageCacher(val appPath: String) {

  def isCached(path: Path): Boolean = Files.exists(path)

  def getFromCache(path: Path): File = new File(path.toString)

  def cache(image: BufferedImage, cachePath: Path): File = {
    val outputFile = new File(cachePath.toString)
    //TODO: create category if does not exist. Now it fails with NullReferenceException
    ImageIO.write(image, "png", outputFile)
    outputFile
  }

  def getImage(picture: Picture, width: Int, height: Int): File = {
    val cachePath = Paths.get(appPath, "data", "images", "cache", width + "x" + height, picture.path)
    if (isCached(cachePath))  {
      getFromCache(cachePath)
    } else {
      val file = getOriginal(Paths.get(appPath, "data", "images", "originals", picture.path))
      val image = ImageResizer.resize(new FileInputStream(file), width, height)
      cache(image, cachePath)
    }
  }
  private def getOriginal(path: Path): File = {
    new File(path.toString)
  }
}
