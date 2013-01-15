package helpers

import java.io.{FileInputStream, File}
import models.Picture
import java.awt.image.BufferedImage
import java.nio.file._
import javax.imageio.ImageIO
import java.awt.{Dimension, Rectangle}

class ImageCacher(val appPath: String) {

  def getImage(picture: Picture, outputDimension: Dimension): File = {
    val cachePath = Paths.get(appPath, "data", "images", "cache", outputDimension.width + "x" + outputDimension.height, picture.path)
    if (Files.exists(cachePath)) {
      new File(cachePath.toString)
    } else {
      val originalFile = new File(Paths.get(appPath, "data", "images", "originals", picture.path).toString)
      val resizedImage: BufferedImage = ImageResizer.resize(originalFile, outputDimension)
      cache(resizedImage, cachePath)
    }
  }

  private def cache(image: BufferedImage, cachePath: Path): File = {
    val outputFile = new File(cachePath.toString)
    if (!outputFile.getParentFile.exists)
      outputFile.getParentFile.mkdirs
    //PNG generates the best quality for thumbnails
    ImageIO.write(image, "png", outputFile)
    outputFile
  }
}
