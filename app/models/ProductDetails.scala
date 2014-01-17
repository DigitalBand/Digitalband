package models

class ProductDetails(
   val title: String,
   val description: String,
   val shortDescription: String,
   val price: Double,
   val id: Int,
   val defaultImageId: Int,
   val brand: BrandEntity,
   val category: CategoryEntity,
   val isAvailable: Boolean) {

  def this(title: String, description: String, shortDescription: String,
  price: Double, id: Int, defaultImageId: Int,
  brand: BrandEntity, category: CategoryEntity) =
    this(title, description, shortDescription, price, id, defaultImageId, brand, category, false)

  def this(categoryId: Int, brandName: String) =
    this("", "", "", 0.0, 0, 0, new BrandEntity(brandName), new CategoryEntity(categoryId, ""))

  def this(title:String, description:String,shortDescription: String,price:Double,id: Int,defaultImageId:Int,category:CategoryEntity,brand:BrandEntity) =
   this(title, description, shortDescription, price, id, defaultImageId, brand, category)

  def this(title: String, description: String, price: Double, id: Int, defaultImageId: Int, brand: BrandEntity) =
    this(title, description, "", price, id, defaultImageId, brand, new CategoryEntity(0))

  def this(title: String, description: String, shortDescription: String, price: Double, id: Int, defaultImageId: Int) =
    this(title, description, shortDescription, price, id, defaultImageId, new BrandEntity(), new CategoryEntity(0))

  def this(title: String, description: String, price: Double, id: Int, defaultImageId: Int) =
    this(title, description, "", price, id, defaultImageId, new BrandEntity(), new CategoryEntity(0))
}

object ProductDetails {
  def apply(id: Option[Int], title:String, description:String, shortDescription:String, price:Double, brandName:String, categoryId: Int, isAvailable: Boolean) =
    new ProductDetails(title, description, shortDescription, price, id.getOrElse(0), 0, new BrandEntity(brandName), new CategoryEntity(categoryId), isAvailable)
  def unapply(d: ProductDetails) = {
    Some(Tuple8(Some(d.id), d.title, d.description, d.shortDescription, d.price, d.brand.title, d.category.id, d.isAvailable))
  }
}





