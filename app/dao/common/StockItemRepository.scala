package dao.common

import models.{ShopListItem, StockItemInfo}

import scala.concurrent.Future

trait StockItemRepository {
  def create(productId: Int, stockItem: StockItemInfo): Future[Int]
  def remove(stockItemId: Int): Future[Int]
  def list(productId: Int): Future[Seq[StockItemInfo]]
  def update(stockItem: StockItemInfo): Future[Int]
  def shopList(productId: Int): Future[Seq[ShopListItem]]
}
