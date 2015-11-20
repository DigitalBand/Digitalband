package dao.impl.fake

import models.{CItem, CartItem}

import scala.concurrent.Future

class CartRepository extends dao.common.CartRepository {
  override def mergeShoppingCarts(authenticatedUserId: Int, anonymousUserId: Int): Unit = ???

  override def updateItems(cartId: Int, items: Seq[CItem]): Unit = ???

  override def deleteItem(cartId: Int, productId: Int): Future[Int] = ???

  override def list(cartId: Int): Future[Seq[CartItem]] = ???

  override def add(item: CartItem): Future[Int] = ???
}
