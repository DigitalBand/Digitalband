package dao.common

import models.DeliveryInfo

trait OrderRepository {
  def create(deliveryInfo: DeliveryInfo, cartId: Int): Int

}
