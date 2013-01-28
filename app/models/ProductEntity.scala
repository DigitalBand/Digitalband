package models

case class ProductEntity(title:String, description: String, price: Double, id: Int, defaultImageId: Int)
case class ProductDetails(title:String, description: String, price: Double, id: Int, defaultImageId: Int, brand: BrandEntity)





