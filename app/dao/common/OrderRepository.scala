package dao.common

import models.{OrderInfo, CartItem, DeliveryInfo}

trait OrderRepository {
  def get(orderId: Int): Option[OrderInfo]

  def getItems(orderId: Int): Seq[CartItem]

  def create(deliveryInfo: DeliveryInfo, userId: Int): Int

}
