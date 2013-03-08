package dao.common

import models.{OrderInfo, CartItem, DeliveryInfo}

trait OrderRepository {
  def get(orderId: Int): Option[OrderInfo] = {
    if (exists(orderId))
      Option(new OrderInfo(getDeliveryInfo(orderId), getItems(orderId)))
    else
      None
  }

  def exists(orderId: Int): Boolean

  def getDeliveryInfo(orderId: Int): DeliveryInfo

  def getItems(orderId: Int): Seq[CartItem]

  def create(deliveryInfo: DeliveryInfo, userId: Int): Int

}
