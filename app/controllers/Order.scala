package controllers

import _root_.models.{DeliveryInfo, DeliveryAddress, OrderInfo}
import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CartRepository, OrderRepository, UserRepository}
import helpers.{EmailHelper, withUser}
import play.api.data.Form
import play.api.data.Forms._

class Order @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository, cartRepository: CartRepository) extends ControllerBase {
//  val deliveryForm = Form(mapping(
//    "lastName" -> nonEmptyText(minLength = 2, maxLength = 50),
//    "name" -> nonEmptyText(minLength = 2, maxLength = 50),
//    "middleName" -> text,
//    "email" -> optional(email),
//    "phone" -> nonEmptyText(minLength = 10, maxLength = 25),
//    "address" -> text
//  )(DeliveryInfo.apply)(DeliveryInfo.unapply))
  val deliveryForm = Form(mapping(
    "city" -> nonEmptyText(minLength = 2, maxLength = 50),
    "street" -> nonEmptyText(minLength = 2, maxLength = 50),
    "building" -> nonEmptyText(minLength = 2, maxLength = 50),
    "housing" -> optional(text),
    "apartment" -> optional(text)
  )(DeliveryAddress.apply)(DeliveryAddress.unapply))
  val emailHelper = new EmailHelper()

  def fill = withUser {
    implicit user =>
      implicit request =>
        val itemsList = cartRepository.list(getUserId)
        if (!itemsList.isEmpty)
          Ok {
            val deliveryAddress = new DeliveryAddress() // userRepository.getDeliveryInfo(getUserId).getOrElse(new DeliveryInfo())
            val form = deliveryForm.fill(deliveryAddress)
            views.html.Order.fill(itemsList, form)
          }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
        else
          Redirect(routes.Cart.display())
  }

  def updateDeliveryAddress = withUser {
    implicit user =>
      implicit request =>
        deliveryForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(views.html.Order.fill(cartRepository.list(getUserId), formWithErrors))
          },
          deliveryAddress => {
            userRepository.updateDeliveryAddress(deliveryAddress, getUserId)
            Redirect(routes.Order.fillPersonal()) //orderId))
          }
        )
  }

  def fillPersonal() = withUser {
    implicit user =>
      implicit request =>
        val itemsList = cartRepository.list(getUserId)
        if (!itemsList.isEmpty)
          Ok {
            val deliveryInfo = new DeliveryAddress()// userRepository.getDeliveryInfo(getUserId).getOrElse(new DeliveryInfo())
            val form = deliveryForm.fill(deliveryInfo)
            views.html.Order.fillPersonal(itemsList, form)
          }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
        else
          Redirect(routes.Cart.display())
  }

  //TODO: rename to "create"
  def create = withUser {
    implicit user =>
      implicit request =>
        deliveryForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(views.html.Order.fill(cartRepository.list(getUserId), formWithErrors))
          },
          deliveryInfo => {
            //userRepository.updateDeliveryInfo(deliveryInfo, getUserId)
            //val orderId = orderRepository.create(deliveryInfo, getUserId)
            //emailHelper.orderConfirmation(new OrderInfo(orderId, deliveryInfo, orderRepository.getItems(orderId)))
            Redirect(routes.Order.confirmation(1)) //orderId))
          }
        )
  }

  def confirmation(orderId: Int) = withUser {
    implicit user =>
      implicit request =>
        Ok(views.html.Order.confirmation(orderRepository.getItems(orderId), orderId))
  }

  def display(orderId: Int) = withUser {
    implicit user =>
      implicit request =>
        NotImplemented
  }
}
