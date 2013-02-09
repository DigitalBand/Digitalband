package dao.common

import models.{CItem, CartItem}

trait CartRepository {
  def updateItems(cartId: Int, items: Seq[CItem])

  def deleteItem(cartId: Int, productId: Int)

  def list(cartId: Int): Seq[CartItem]

  def add(item: CartItem): Int
  def createCart(userId: Int): Int
}
