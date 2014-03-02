package controllers

import _root_.models.{OrderInfo, DeliveryInfo}
import common.ControllerBase
import play.api.mvc.{Action}
import com.google.inject.Inject
import dao.common.{UserRepository, CartRepository, OrderRepository}
import play.api._
import data.Form
import data.Forms._
import helpers.{Secured, EmailHelper}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

class Order @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository, cartRepository: CartRepository) extends ControllerBase with Secured {
  val deliveryForm = Form(mapping(
    "name" -> nonEmptyText(minLength = 2, maxLength = 50),
    "email" -> email,
    "phone" -> nonEmptyText(minLength = 10, maxLength = 25),
    "address" -> text
  )(DeliveryInfo.apply)(DeliveryInfo.unapply))
  val emailHelper = new EmailHelper()

  def fill = withUser {
    implicit user =>
      implicit request => Future {
        val itemsList = cartRepository.list(getUserId)
        if (!itemsList.isEmpty)
          Ok {
            val deliveryInfo = userRepository.getDeliveryInfo(getUserId).getOrElse(new DeliveryInfo())
            val form = deliveryForm.fill(deliveryInfo)
            views.html.Order.fill(itemsList, form)
          }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
        else
          Redirect(routes.Cart.display())
      }
  }

  //TODO: rename to "create"
  def create = withUser {
    implicit user =>
      implicit request => Future {
        deliveryForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(views.html.Order.fill(cartRepository.list(getUserId), formWithErrors))
          },
          deliveryInfo => {
            userRepository.updateDeliveryInfo(deliveryInfo, getUserId)
            val orderId = orderRepository.create(deliveryInfo, getUserId)
            emailHelper.orderConfirmation(new OrderInfo(orderId, deliveryInfo, orderRepository.getItems(orderId)))
            Redirect(routes.Order.confirmation(orderId))
          }
        )
      }
  }

  def confirmation(orderId: Int) = withUser {
    implicit user =>
      implicit request => Future {
        Ok(views.html.Order.confirmation(orderRepository.getItems(orderId), orderId))
      }
  }

  def display(orderId: Int) = withUser {
    implicit user =>
      implicit request => Future {
        NotImplemented
      }
  }
}
