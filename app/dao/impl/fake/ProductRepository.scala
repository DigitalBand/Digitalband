package dao.impl.fake

import models.{CategoryEntity, ProductUnit}

class ProductRepository extends dao.common.ProductRepository {

  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int): Seq[ProductUnit] = {
    List(
      new models.ProductUnit(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        1, 1),
      new models.ProductUnit(
        "IBANEZ GART60 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
        8351,
        2, 2),
      new models.ProductUnit(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        3, 3),
      new models.ProductUnit(
        "IBANEZ GART60 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
        8351,
        2, 0))
  }
  def listMostVisited(count: Int) = {
    for (i <- 1 to count)
      yield new models.ProductUnit(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        i, 2)

  }
}
