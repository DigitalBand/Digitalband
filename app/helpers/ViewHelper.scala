package helpers

import models.BrandEntity

object ViewHelper {
  def brandId(brand: Option[BrandEntity]): Int = {
    brand match {
      case Some(x) => x.id
      case None => 0
    }
  }
}
