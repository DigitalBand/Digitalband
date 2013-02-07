package models

class CartItem(val id: Int, val productId:Int, val title:String, val imageId:Int, val count:Int, val unitPrice: Double) {
  def this(id: Int, productId: Int, count: Int) = this(id, productId, "", 0, count, 0.0)
  def total = unitPrice*count
}
