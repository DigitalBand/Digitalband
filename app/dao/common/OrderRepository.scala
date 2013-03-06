package dao.common

import models.{CartItem, DeliveryInfo}

trait OrderRepository {
  def getItems(orderId: Int): Seq[CartItem]

  def create(deliveryInfo: DeliveryInfo, userId: Int): Int

}
