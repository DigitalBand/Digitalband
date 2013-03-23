package models


class ProductDetails(val title: String, val description: String,
                          val price: Double, val id: Int, val defaultImageId: Int, val brand: BrandEntity) {
  def this(title: String, description: String, price: Double, id: Int, defaultImageId: Int) =
    this(title, description, price, id, defaultImageId, new BrandEntity(0, "Unknown", 0, 0))
}





