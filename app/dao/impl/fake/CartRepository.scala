package dao.impl.fake

import models.{CItem, CartItem}

class CartRepository extends dao.common.CartRepository {
  def add(item: CartItem): Int = 2

  def list(cartId: Int) = ???

  def deleteItem(cartId: Int, productId: Int) = ???

  def createCart(userId: Int) = ???

  def updateItems(cartId: Int, items: Seq[CItem]) = ???

  def mergeShoppingCarts(authenticatedUserId: Int, anonymousUserId: Int) = ???
}
