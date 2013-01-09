package dao


object ProductRepository {
   def getList(): List[models.Product] = {
     List(
       new models.Product(
         "IBANEZ GRX40 BLACK NIGHT",
         "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
         6828,
         1),
       new models.Product(
         "IBANEZ GART60 BLACK NIGHT",
          "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели H-H, фурнитура хром",
         8351,
       2),
       new models.Product(
       "IBANEZ GRX40 BLACK NIGHT",
       "электрогитара, цвет черный, корпус липа, гриф клен, звукосниматели S-S-H, бридж FAT6, фурнитура хром",
       6828,
       3))
   }
}
