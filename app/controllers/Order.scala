package controllers

import models.{DeliveryInfo}
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

  def fill = Action {
    implicit request =>
      val itemsList = cartRepository.list(getUserId)
      if (!itemsList.isEmpty)
        Ok(views.html.Order.fill(itemsList,
          deliveryForm.fill(
            userRepository.getDeliveryInfo(getUserId).getOrElse(new DeliveryInfo()))))
      else
        Redirect(routes.Cart.display())
  }

  def place = Action {
    implicit request =>
      deliveryForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.Order.fill(cartRepository.list(getUserId), formWithErrors))
        },
        deliveryInfo => {
          userRepository.updateDeliveryInfo(deliveryInfo, getUserId)
          val orderId = orderRepository.create(deliveryInfo, getUserId)

          //TODO: Implement
          EmailHelper.orderConfirmation(orderId, deliveryInfo.email)
          Redirect(routes.Order.confirmation(orderId))
        }
      )
  }

  def confirmation(orderId: Int) = Action {
    implicit request =>
    Ok(views.html.Order.confirmation(orderRepository.getItems(orderId), orderId))
  }

  def display(orderId: Int) = Action {
    NotImplemented
  }
}
