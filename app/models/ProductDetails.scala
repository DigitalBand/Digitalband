package models

class ProductDetails(val title: String, val description: String, val shortDescription: String,
                          val price: Double, val id: Int, val defaultImageId: Int,
                          val brand: BrandEntity, val brandName: String, val categoryId: Int) {
  def this(categoryId: Int, brandName: String) = this("", "", "", 0.0, 0, 0, new BrandEntity(), brandName, categoryId)
  def this(title: String, description: String, price: Double, id: Int, defaultImageId: Int, brand: BrandEntity) =
    this(title, description, "", price, id, defaultImageId, brand, "", 0)

  def this(title: String, description: String, shortDescription: String, price: Double, id: Int, defaultImageId: Int) =
    this(title, description, shortDescription, price, id, defaultImageId, new BrandEntity(), "", 0)

  def this(title: String, description: String, price: Double, id: Int, defaultImageId: Int) =
    this(title, description, "", price, id, defaultImageId, new BrandEntity(), "", 0)
}

object ProductDetails {
  def apply(title:String,description:String,shortDescription:String,price:Double,brandName:String, categoryId: Int) =
    new ProductDetails(title, description, shortDescription, price, 0, 0, new BrandEntity(), brandName, categoryId)
  def unapply(d: ProductDetails) = {
    Some(Tuple6(d.title, d.description, d.shortDescription, d.price, d.brandName, d.categoryId))
  }
}





