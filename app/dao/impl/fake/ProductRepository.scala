package dao.impl.fake

import models._
import models.BrandEntity
import models.CategoryEntity
import models.{ProductDetails => ProductEntity}

class ProductRepository extends dao.common.ProductRepository {

  def getList(getCategory: => CategoryEntity, brandId: Int, pageNumber: Int, pageSize: Int, search: String): ListPage[ProductEntity] = {
    new ListPage(1, List(
      new ProductEntity(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        1, 1),
      new ProductEntity(
        "IBANEZ GART60 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
        8351,
        2, 2),
      new ProductEntity(
        "IBANEZ GRX40 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
        6828,
        3, 3),
      new ProductEntity(
        "IBANEZ GART60 BLACK NIGHT",
        "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
        8351,
        2, 0)), 1)
  }

  def listMostVisited(count: Int) = {
    for (i <- 1 to count)
    yield new ProductEntity(
      "IBANEZ GRX40 BLACK NIGHT",
      "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
      6828,
      i, 2)

  }

  def get(id: Int): ProductEntity = ???

  def get(productId: Int, getBrand: (Int) => Option[BrandEntity]): ProductDetails = ???

  def create(details: ProductDetails, imageId: Int, getBrandId: (String) => Int, userId: Int) = ???

  def update(product: ProductDetails, imageId: Int, getBrandId: String => Int, userId: Int): Int = ???

  def insertImage(imageId: Int, productId: Int) {}

  def update(product: ProductDetails, getBrandId: (String) => Int, userId: Int)(after: => Unit) = ???

  def create(details: ProductDetails, getBrandId: (String) => Int, userId: Int)(after: (Int) => Unit) = ???
}
