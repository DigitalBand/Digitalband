package dao.common

import models.{OrderInfo, CartItem, DeliveryInfo}

trait OrderRepository {
  def listAll(): Seq[OrderInfo]

  def get(orderId: Int): OrderInfo = new OrderInfo(getDeliveryInfo(orderId), getItems(orderId))

  def exists(orderId: Int): Boolean

  def getDeliveryInfo(orderId: Int): DeliveryInfo

  def getItems(orderId: Int): Seq[CartItem]

  def create(deliveryInfo: DeliveryInfo, userId: Int): Int

}
