package dao.common

import models.{ListPage, OrderInfo, CartItem, DeliveryInfo}

trait OrderRepository {
  def delete(orderId: Int)

  def changeStatus(orderId: Int, status: String)

  def listAll(pageNumber: Int, pageSize: Int): ListPage[OrderInfo]

  def get(orderId: Int): OrderInfo = new OrderInfo(orderId, getDeliveryInfo(orderId), getItems(orderId))

  def exists(orderId: Int): Boolean

  def getDeliveryInfo(orderId: Int): DeliveryInfo

  def getItems(orderId: Int): Seq[CartItem]

  def create(deliveryInfo: DeliveryInfo, userId: Int): Int

}
