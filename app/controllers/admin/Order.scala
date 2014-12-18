package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{OrderRepository, UserRepository, ShopRepository}
import helpers.{EmailHelper, withAdmin}
import models._
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import views.html.Admin.Order

class Order @Inject()(implicit userRepository: UserRepository, orderRepository: OrderRepository, shopRepository: ShopRepository) extends ControllerBase {
  val orderStatusForm = Form("comment" -> text)
  val emailHelper = new EmailHelper()

  def list(pageNumber: Int = 1, pageSize: Int) = withAdmin { implicit user =>
      implicit request =>
        val orders: ListPage[models.OrderInfo] = orderRepository.listAll(pageNumber, pageSize)
        val orderCounters: Seq[(String, Int)] = orderRepository.getCounters
        Ok(Order.list(orders, orderCounters, pageSize, pageNumber))
  }

  def display(id: Int) = withAdmin { implicit user =>
      implicit request =>
        val order = orderRepository.get(id)
        if(order.deliveryType == Messages("internationalDelivery"))
          Ok(Order.display(getDeliveryOrder(order)))
        else if(order.deliveryType == Messages("internationalPickup"))
          Ok(Order.display(getPickupOrder(order)))
        else
          Ok(Order.display(order))
  }

  //TODO: It is not a good name, rename it
  def getDeliveryOrder(order: OrderInfo): OrderInfo = {
    val orderDeliveryInfo = orderRepository.getOrderDeliveryInfo(order.id)
    val deliveryInfo = new DeliveryInfo(
      orderDeliveryInfo.personalInfo.toString(),
      orderDeliveryInfo.personalInfo.email.getOrElse(""),
      orderDeliveryInfo.personalInfo.phone,
      orderDeliveryInfo.address.toString())
    val orderInfo = new OrderInfo(
      order.id,
      order.orderDate,
      order.status,
      Messages("delivery"),
      order.comment,
      deliveryInfo,
      order.items)
    orderInfo
  }

  def getPickupOrder(order: OrderInfo): OrderInfo = {
    val pickupDeliveryInfo = orderRepository.getPickupDeliveryInfo(order.id)
    val shop = shopRepository.get(pickupDeliveryInfo.shopId);
    val deliveryInfo = new DeliveryInfo(
      pickupDeliveryInfo.personalInfo.toString(),
      pickupDeliveryInfo.personalInfo.email.getOrElse(""),
      pickupDeliveryInfo.personalInfo.phone,
      shop.address)
    val orderInfo = new OrderInfo(
      order.id,
      order.orderDate,
      order.status,
      Messages("pickup"),
      order.comment,
      deliveryInfo,
      order.items)
    orderInfo
  }

  def confirm(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        val comment = orderStatusForm.bindFromRequest().get
        orderRepository.changeStatus(orderId, "confirmed")
        emailHelper.orderConfirmed(comment, orderRepository.get(orderId))
        Redirect(controllers.admin.routes.Order.display(orderId))
  }

  def cancel(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        val comment = orderStatusForm.bindFromRequest().get
        orderRepository.changeStatus(orderId, "canceled")
        emailHelper.orderCanceled(comment, orderRepository.get(orderId))
        Redirect(controllers.admin.routes.Order.display(orderId))
  }

  def complete(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        orderRepository.changeStatus(orderId, "done")
        Redirect(controllers.admin.routes.Order.display(orderId))
  }

  def delete(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        orderRepository.delete(orderId)
        Redirect(controllers.admin.routes.Order.list(1))
  }

  def confirmPage(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(Order.confirmPage(orderRepository.get(orderId)))
  }

  def cancelPage(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(Order.cancelPage(orderRepository.get(orderId)))
  }

  def completePage(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(Order.completePage(orderId))
  }

  def deletePage(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(Order.deletePage(orderId))
  }
}
