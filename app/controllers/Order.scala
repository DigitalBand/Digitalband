package controllers

import models.{DeliveryInfo}
import common.ControllerBase
import play.api.mvc.{Action}
import com.google.inject.Inject
import dao.common.{UserRepository, CartRepository, OrderRepository}
import play.api._
import data.Form
import data.Forms._

class Order @Inject()(orderRepository: OrderRepository, cartRepository: CartRepository, ur: UserRepository) extends ControllerBase(ur) {
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
        Ok(views.html.Order.fill(itemsList, deliveryForm))
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
          val userId = userRepository.getUserId(deliveryInfo.email) match {
            case 0 => userRepository.createUser(deliveryInfo.email)
            case x => x
          }
          val orderId = orderRepository.create(deliveryInfo, userId, userId)
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
