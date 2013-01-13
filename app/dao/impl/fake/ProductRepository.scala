package dao.impl.fake

import models.ProductUnit

class ProductRepository extends dao.common.ProductRepository {
   def getList(): List[ProductUnit] = {
     List(
       new models.ProductUnit(
         "IBANEZ GRX40 BLACK NIGHT",
         "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
         6828,
         1),
       new models.ProductUnit(
         "IBANEZ GART60 BLACK NIGHT",
          "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
         8351,
       2),
       new models.ProductUnit(
       "IBANEZ GRX40 BLACK NIGHT",
       "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
       6828,
       3))
   }
}
