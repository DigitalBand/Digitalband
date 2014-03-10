package dao.common

import models.ShopInfo

trait ShopRepository {
  def remove(shopId: Int)
  def add(shop: ShopInfo): Int
  def update(shop: ShopInfo)
  def list: Seq[ShopInfo]
}
