package models
case class CItem(productId: Int, count: Int, returnUrl: String)
class CartItem(val userId: Int, val productId:Int, val title:String, val imageId:Int, val count:Int, val unitPrice: Double) {
  def this(userId: Int, productId: Int, count: Int) = this(userId, productId, "", 0, count, 0.0)
  def total = unitPrice*count
}
