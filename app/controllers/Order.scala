package controllers

import models.{PersonalInfo, DeliveryInfo, DeliveryAddress, OrderInfo}
import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CartRepository, OrderRepository, UserRepository}
import helpers.{EmailHelper, withUser}
import play.api.data.Form
import play.api.data.Forms._

class Order @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository, cartRepository: CartRepository) extends ControllerBase {
  val deliveryPersonalForm = Form(mapping(
    "lastName" -> nonEmptyText(minLength = 2, maxLength = 50),
    "firstName" -> nonEmptyText(minLength = 2, maxLength = 50),
    "middleName" -> text,
    "phone" -> nonEmptyText(minLength = 10, maxLength = 25),
    "email" -> optional(email)
  )(PersonalInfo.apply)(PersonalInfo.unapply))

  val pickupPersonalForm = Form(mapping(
    "lastName" -> text,
    "firstName" -> text,
    "middleName" -> text,
    "phone" -> nonEmptyText(minLength = 10, maxLength = 25),
    "email" -> optional(email)
  )(PersonalInfo.apply)(PersonalInfo.unapply))

  val deliveryForm = Form(mapping(
    "city" -> nonEmptyText(minLength = 2, maxLength = 50),
    "street" -> nonEmptyText(minLength = 2, maxLength = 50),
    "building" -> nonEmptyText(minLength = 1, maxLength = 50),
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
            val deliveryAddress = userRepository.getDeliveryAddress(getUserId).getOrElse(new DeliveryAddress())
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
            Redirect(routes.Order.fillPersonal(1))
          }
        )
  }

  def fillPersonal(deliveryType: Int) = withUser {
    implicit user =>
      implicit request =>
        val itemsList = cartRepository.list(getUserId)
        if (!itemsList.isEmpty)
          Ok {
            val personalInfo = userRepository.getPersonalInfo(getUserId).getOrElse(new PersonalInfo())
            val form =
              if (deliveryType == 1)
                deliveryPersonalForm.fill(personalInfo)
              else
                pickupPersonalForm.fill(personalInfo)
            views.html.Order.fillPersonal(itemsList, form)
          }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
        else
          Redirect(routes.Cart.display())
  }

  def create = withUser {
    implicit user =>
      implicit request =>
        deliveryPersonalForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(views.html.Order.fillPersonal(cartRepository.list(getUserId), formWithErrors))
          },
          personalInfo => {
            userRepository.updatePersonalInfo(personalInfo, getUserId)
            val deliveryAddress = userRepository.getDeliveryAddress(getUserId).getOrElse(new DeliveryAddress())
            val orderId = orderRepository.create(getUserId, deliveryAddress, personalInfo)
            //emailHelper.orderConfirmation(new OrderInfo(orderId, deliveryInfo, orderRepository.getItems(orderId)))
            Redirect(routes.Order.confirmation(orderId))
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
