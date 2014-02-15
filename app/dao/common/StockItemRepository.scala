package dao.common

import controllers.admin.StockItemInfo

trait StockItemRepository {
  def create(productId: Int, stockItem: StockItemInfo): Int
  def list(productId: Int): Seq[StockItemInfo]
}
