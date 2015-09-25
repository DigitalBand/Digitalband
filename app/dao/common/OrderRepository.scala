package dao.common

import models._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait OrderRepository {
  def groupUnconfirmedByHost: Future[Map[Option[String], Int]]

  def getCounters: Future[Seq[(String, Int)]]

  def delete(orderId: Int): Future[Int]

  def changeStatus(orderId: Int, status: String): Future[Int]

  def listAll(pageNumber: Int, pageSize: Int): Future[ListPage[OrderInfo]]

  def get(orderId: Int): Future[OrderInfo] =
    for {
      deliveryInfo <- getDeliveryInfo(orderId)
      items <- getItems(orderId)
    } yield new OrderInfo(orderId, deliveryInfo, items)

  def exists(orderId: Int): Future[Boolean]

  def getDeliveryInfo(orderId: Int): Future[DeliveryInfo]

  def getOrderDeliveryInfo(orderId: Int): Future[OrderDeliveryInfo]

  def getPickupDeliveryInfo(orderId: Int): Future[PickupDeliveryInfo]

  def getItems(orderId: Int): Future[Seq[CartItem]]

  def create[TDeliveryInfo](userId: Int, cityId: Option[Int], deliveryInfo: TDeliveryInfo): Future[Int]
}
