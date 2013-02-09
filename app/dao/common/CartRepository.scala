package dao.common

import models.CartItem

trait CartRepository {
  def deleteItem(cartId: Int, productId: Int)

  def list(cartId: Int): Seq[CartItem]

  def add(item: CartItem): Int
  def createCart(userId: Int): Int
}
