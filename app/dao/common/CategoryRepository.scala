package dao.common

import models.CategoryEntity

trait CategoryRepository {
  def getListWithPictures(): Seq[CategoryEntity]

}
