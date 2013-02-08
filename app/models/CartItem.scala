package models

class CartItem(val cartId: Int, val productId:Int, val title:String, val imageId:Int, val count:Int, val unitPrice: Double) {
  def this(cartId: Int, productId: Int, count: Int) = this(cartId, productId, "", 0, count, 0.0)
  def total = unitPrice*count
}
