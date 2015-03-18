package dao.common

import models.{CItem, CartItem}

trait CartRepository {
  def mergeShoppingCarts(authenticatedUserId: Int, anonymousUserId: Int)

  def updateItems(cartId: Int, items: Seq[CItem])

  def deleteItem(cartId: Int, productId: Int)

  def list(cartId: Int): Iterator[CartItem]

  def add(item: CartItem): Int
}
