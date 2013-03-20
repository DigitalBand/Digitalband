package controllers

import _root_.models.{OrderInfo, DeliveryInfo}
import common.ControllerBase
import play.api.mvc.{Action}
import com.google.inject.Inject
import dao.common.{UserRepository, CartRepository, OrderRepository}
import play.api._
import data.Form
import data.Forms._
import helpers.EmailHelper

class Order @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository, cartRepository: CartRepository) extends ControllerBase {
  val deliveryForm = Form(mapping(
    "name" -> nonEmptyText(minLength = 2, maxLength = 50),
    "email" -> email,
    "phone" -> nonEmptyText(minLength = 10, maxLength = 25),
    "address" -> text
  )(DeliveryInfo.apply)(DeliveryInfo.unapply))
  val emailHelper = new EmailHelper()
  def fill = withUser { implicit user =>
    implicit request =>
      val itemsList = cartRepository.list(getUserId)
      if (!itemsList.isEmpty)
        Ok(views.html.Order.fill(itemsList,
          deliveryForm.fill(
            userRepository.getDeliveryInfo(getUserId).getOrElse(new DeliveryInfo()))))
      else
        Redirect(routes.Cart.display())
  }

  //TODO: rename to "create"
  def place = withUser { implicit user =>
    implicit request =>
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

  def confirmation(orderId: Int) = withUser { implicit user =>
    implicit request =>
    Ok(views.html.Order.confirmation(orderRepository.getItems(orderId), orderId))
  }

  def display(orderId: Int) = withUser { implicit user =>
    implicit request =>
      NotImplemented
  }
}
