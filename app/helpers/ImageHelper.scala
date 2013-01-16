package helpers

import java.awt.{Dimension}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.{IIOImage, ImageWriteParam, ImageIO}
import javax.imageio.stream.FileImageOutputStream

object ImageHelper {
  def getDimension(imageSize: String) = {
    val arr = imageSize.split("x").map(_.toInt)
    new Dimension(arr(0), arr(1))
  }
  def getImageNumber(imageNumber: String): Int = {
    imageNumber.split("[\\.]")(0).toInt
  }
  def write(image: BufferedImage, outputFile: File, quality: Float): File = {
    // Just get the first JPEG writer available
    val jpegWriter = ImageIO.getImageWritersByFormatName("jpeg").next()
    try {
      // Set the compression quality to 0.8
      val param: ImageWriteParam = jpegWriter.getDefaultWriteParam()
      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT)
      param.setCompressionQuality(quality)
      // Write the image to a file
      val out: FileImageOutputStream = new FileImageOutputStream(outputFile)
      try {
        jpegWriter.setOutput(out)
        jpegWriter.write(null, new IIOImage(image, null, null), param)
        outputFile
      } finally {
        out.close()
      }
    } finally {
      jpegWriter.dispose()
    }
  }
}
