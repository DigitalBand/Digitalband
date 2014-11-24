package dao.common

import models._

trait OrderRepository {
  def countUnconfirmed: Map[String, Int]

  def getCounters: Seq[(String, Int)]

  def delete(orderId: Int)

  def changeStatus(orderId: Int, status: String)

  def listAll(pageNumber: Int, pageSize: Int): ListPage[OrderInfo]

  def get(orderId: Int): OrderInfo = new OrderInfo(orderId, getDeliveryInfo(orderId), getItems(orderId))

  def exists(orderId: Int): Boolean

  def getDeliveryInfo(orderId: Int): DeliveryInfo

  def getOrderDeliveryInfo(orderId: Int): OrderDeliveryInfo

  def getPickupDeliveryInfo(orderId: Int): PickupDeliveryInfo

  def getItems(orderId: Int): Seq[CartItem]

  def create[TDeliveryInfo](userId: Int, cityId: Option[Int], deliveryInfo: TDeliveryInfo): Int
}
