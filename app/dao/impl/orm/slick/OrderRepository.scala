package dao.impl.orm.slick

import models.DeliveryInfo

class OrderRepository extends dao.common.OrderRepository {
  def create(deliveryInfo: DeliveryInfo, cartId: Int): Int = {
    ???
  }
}
