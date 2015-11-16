package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{OrderRepository, UserRepository, ShopRepository}
import helpers.{EmailHelper, withAdmin}
import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.api.libs.mailer.MailerClient
import views.html.Admin.Order
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Order @Inject()(implicit userRepository: UserRepository,
                      orderRepository: OrderRepository,
                      shopRepository: ShopRepository, mailerClient: MailerClient) extends ControllerBase {
  val orderStatusForm = Form("comment" -> text)
  val emailHelper = new EmailHelper()

  def list(pageNumber: Int = 1, pageSize: Int) = withAdmin.async { implicit user =>
    implicit request => for {
      orders <- orderRepository.listAll(pageNumber, pageSize)
      orderCounters <- orderRepository.getCounters
    } yield Ok(Order.list(orders, orderCounters, pageSize, pageNumber))
  }

  def getOrder(id: Int) = {
    for {
      order <- orderRepository.get(id)
      /*deliveryOrder <- getDeliveryOrder(order) if order.deliveryType == "Delivery"
      pickupOrder <- getPickupOrder(order) if order.deliveryType == "Pickup"*/
    } yield {
      //TODO: Implement this
      /*if (order.deliveryType == "Delivery")
        deliveryOrder
      else if (order.deliveryType == "Pickup")
        pickupOrder
      else*/
        order
    }
  }
  def display(id: Int) = withAdmin.async { implicit user =>
    implicit request =>
      for {
        order <- getOrder(id)
      } yield {
        Ok(Order.display(order))
      }
  }

  //TODO: It is not a good name, rename it
  def getDeliveryOrder(order: OrderInfo): Future[OrderInfo] =
    for {
      orderDeliveryInfo <- orderRepository.getOrderDeliveryInfo(order.id)
    } yield {
      val deliveryInfo = new DeliveryInfo(
        orderDeliveryInfo.personalInfo.toString(),
        orderDeliveryInfo.personalInfo.email.getOrElse(""),
        orderDeliveryInfo.personalInfo.phone,
        orderDeliveryInfo.address.toString()
      )
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


  def getPickupOrder(order: OrderInfo): Future[OrderInfo] = for {
    pickupDeliveryInfo <- orderRepository.getPickupDeliveryInfo(order.id)
    shop <- shopRepository.get(pickupDeliveryInfo.shopId)
  } yield {
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

  def confirm(orderId: Int) = withAdmin.async { implicit user =>
    implicit request =>
      for {
        c <- orderRepository.changeStatus(orderId, "confirmed")
        order <- orderRepository.get(orderId)
      } yield {
        val comment = orderStatusForm.bindFromRequest().get
        emailHelper.orderConfirmed(comment, order)
        Redirect(controllers.admin.routes.Order.display(orderId))
      }
  }

  def cancel(orderId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        for {
          order <- orderRepository.get(orderId)
          c <- orderRepository.changeStatus(orderId, "canceled")
        } yield {
          val comment = orderStatusForm.bindFromRequest().get
          emailHelper.orderCanceled(comment, order)
          Redirect(controllers.admin.routes.Order.display(orderId))
        }
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

  def confirmPage(orderId: Int) = withAdmin.async { implicit user =>
    implicit request => orderRepository.get(orderId).map { order =>
      Ok(Order.confirmPage(order))
    }
  }

  def cancelPage(orderId: Int) = withAdmin.async { implicit user =>
    implicit request => orderRepository.get(orderId).map { order =>
      Ok(Order.cancelPage(order))
    }
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
