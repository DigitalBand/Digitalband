package dao.common

import models.{ShopListItem, StockItemInfo}

trait StockItemRepository {
  def create(productId: Int, stockItem: StockItemInfo): Int
  def remove(stockItemId: Int)
  def list(productId: Int): Iterator[StockItemInfo]
  def update(stockItem: StockItemInfo)
  def shopList(productId: Int): Seq[ShopListItem]
}
