package dao.common

import models.{CItem, CartItem}

import scala.concurrent.Future

trait CartRepository {
  def mergeShoppingCarts(authenticatedUserId: Int, anonymousUserId: Int)

  def updateItems(cartId: Int, items: Seq[CItem])

  def deleteItem(cartId: Int, productId: Int): Future[Int]

  def list(cartId: Int): Future[Seq[CartItem]]

  def add(item: CartItem): Future[Int]
}
