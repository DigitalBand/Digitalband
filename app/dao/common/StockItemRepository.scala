package dao.common

import controllers.admin.StockItemInfo

trait StockItemRepository {
  def create(productId: Int, stockItem: StockItemInfo): Int
  def remove(stockItemId: Int)
  def list(productId: Int): Seq[StockItemInfo]
  def update(stockItem: StockItemInfo)
}
