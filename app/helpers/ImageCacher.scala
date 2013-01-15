package helpers

import java.io.{FileInputStream, File}
import models.Picture
import java.awt.image.BufferedImage
import java.nio.file._
import javax.imageio.ImageIO
import java.awt.Rectangle

class ImageCacher(val appPath: String) {

  def isCached(path: Path): Boolean = Files.exists(path)

  def getFromCache(path: Path): File = new File(path.toString)

  def cache(image: BufferedImage, cachePath: Path): File = {
    val outputFile = new File(cachePath.toString)
    //TODO: create category if does not exist. Now it fails with NullReferenceException
    if (!outputFile.getParentFile.exists)
      outputFile.getParentFile.mkdirs
    ImageIO.write(image, "png", outputFile)
    outputFile
  }

  def getImage(picture: Picture, rect: Rectangle): File = {
    val cachePath = Paths.get(appPath, "data", "images", "cache", rect.width + "x" + rect.height, picture.path)
    if (isCached(cachePath)) {
      getFromCache(cachePath)
    } else {
      val file = getOriginal(Paths.get(appPath, "data", "images", "originals", picture.path))
      val imageResizer = new ImageResizer()
      val image = imageResizer.resize(new FileInputStream(file), rect)
      cache(image, cachePath)
    }
  }

  private def getOriginal(path: Path): File = {
    new File(path.toString)
  }
}
