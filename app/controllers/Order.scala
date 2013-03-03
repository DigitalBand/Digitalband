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

  def getCartId(implicit sess: play.api.mvc.Session): Int =
    helpers.SessionHelper.getCartId(sess, cartRepository.createCart, userRepository.getUserId)

  def fill(checkoutMethod: String) = Action {
    implicit request =>
      val cartId = getCartId(session)
      Ok(views.html.Order.fill(cartRepository.list(cartId), deliveryForm))
  }

  def place = Action {
    implicit request =>
      deliveryForm.bindFromRequest.fold(
        formWithErrors => {
          val cartId = getCartId(session)
          BadRequest(views.html.Order.fill(cartRepository.list(cartId), formWithErrors))
        },
        deliveryInfo => {
          val cartId:Int = getCartId
          val userId = userRepository.getUserId(deliveryInfo.email) match {
            case 0 => userRepository.createUser(deliveryInfo.email)
            case x => x
          }
          val orderId = orderRepository.create(deliveryInfo, cartId, userId)
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
