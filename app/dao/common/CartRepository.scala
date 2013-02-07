package dao.common

import models.CartItem

trait CartRepository {
  def list(cartId: Int): Seq[CartItem]

  def add(item: CartItem): Int

}
