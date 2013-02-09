package dao.impl.fake

import models.CartItem

class CartRepository extends dao.common.CartRepository {
  def add(item: CartItem): Int = 2

  def list(cartId: Int): Seq[CartItem] = List(new CartItem(1, 1, "Test", 1, 2, 45355), new CartItem(1, 1, "Test", 1, 1, 66555))

  def deleteItem(cartId: Int, productId: Int) = ???
}
