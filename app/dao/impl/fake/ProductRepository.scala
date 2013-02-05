package dao.impl.fake

import models._
import models.BrandEntity
import models.CategoryEntity
import models.ProductEntity

class ProductRepository extends dao.common.ProductRepository {

  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int, search:String): ListPage[ProductEntity] = {
    new ListPage(1, List(
      new models.ProductEntity(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        1, 1),
      new models.ProductEntity(
        "IBANEZ GART60 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
        8351,
        2, 2),
      new models.ProductEntity(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        3, 3),
      new models.ProductEntity(
        "IBANEZ GART60 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
        8351,
        2, 0)), 1)
  }
  def listMostVisited(count: Int) = {
    for (i <- 1 to count)
      yield new models.ProductEntity(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        i, 2)

  }
  def get(id: Int): ProductEntity = ???

  def get(productId: Int, getBrand: (Int) => Option[BrandEntity]): ProductDetails = ???
}
