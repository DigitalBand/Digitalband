package controllers

import dao.impl.orm.slick.ShopRepository
import models._
import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CartRepository, OrderRepository, UserRepository}
import helpers.{EmailHelper, withUser}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages

class Order @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository, cartRepository: CartRepository, shopRepository: ShopRepository) extends ControllerBase {
  val deliveryForm = Form(mapping(
    "address" -> mapping(
      "city" -> nonEmptyText(minLength = 2, maxLength = 50),
      "street" -> nonEmptyText(minLength = 2, maxLength = 50),
      "building" -> nonEmptyText(minLength = 1, maxLength = 50),
      "apartment" -> optional(text)
    )(DeliveryAddress.apply)(DeliveryAddress.unapply),
    "personalInfo" -> mapping(
      "lastName" -> nonEmptyText(minLength = 2, maxLength = 50),
      "firstName" -> nonEmptyText(minLength = 2, maxLength = 50),
      "middleName" -> text,
      "phone" -> nonEmptyText(minLength = 10, maxLength = 25),
      "email" -> optional(email),
      "password" -> optional(text)
    )(PersonalInfo.apply)(PersonalInfo.unapply),
    "comment" -> text,
    "register" -> boolean
  )(OrderDeliveryInfo.apply)(OrderDeliveryInfo.unapply)
    verifying("Заполнить все немедленно",
      fields => fields match {
        case deliveryInfo => validatePersonalInfo(
          deliveryInfo.personalInfo.email.getOrElse(""),
          deliveryInfo.personalInfo.password.getOrElse(""),
          deliveryInfo.register
        ) == false
      }
    )
  )

  val pickupForm = Form(mapping(
    "shopId" -> number,
    "personalInfo" -> mapping(
      "lastName" -> nonEmptyText(minLength = 2, maxLength = 50),
      "firstName" -> nonEmptyText(minLength = 2, maxLength = 50),
      "middleName" -> text,
      "phone" -> nonEmptyText(minLength = 10, maxLength = 25),
      "email" -> optional(email),
      "password" -> optional(text)
    )(PersonalInfo.apply)(PersonalInfo.unapply),
    "comment" -> text,
    "register" -> boolean
  )(PickupDeliveryInfo.apply)(PickupDeliveryInfo.unapply)
    verifying("Заполнить все немедленно",
      fields => fields match {
        case deliveryInfo => validatePersonalInfo(
          deliveryInfo.personalInfo.email.getOrElse(""),
          deliveryInfo.personalInfo.password.getOrElse(""),
          deliveryInfo.register
        ) == false
      }
    )
  )

  val emailHelper = new EmailHelper()

  def validatePersonalInfo(email: String, password: String, register: Boolean) = {
    Logger.info("validate personal info")
    if(register){
      false
    }
    true
  }

  def fill = withUser {
    implicit user =>
      implicit request =>
        val itemsList = cartRepository.list(getUserId)
        if (!itemsList.isEmpty)
          Ok {
            views.html.Order.fill(itemsList)
          }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
        else
          Redirect(routes.Cart.display())
  }

  def fillDelivery() = withUser {
    implicit user =>
      implicit request =>
        val itemsList = cartRepository.list(getUserId)
        if (!itemsList.isEmpty)
          Ok {
            val userInfo = userRepository.getUserInfo(getUserId).getOrElse(new UserInfo())
            val deliveryInfo = new OrderDeliveryInfo(
              address = userInfo.address.get,
              personalInfo = userInfo.personalInfo,
              comment = "",
              register = false)
            val form = deliveryForm.fill(deliveryInfo)
            views.html.Order.fillDelivery(itemsList, form)

          }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
        else
          Redirect(routes.Cart.display())
  }

  def fillPickup = withUser {
    implicit user =>
      implicit request =>
        val itemsList = cartRepository.list(getUserId)
        if (!itemsList.isEmpty)
          Ok {
            val userInfo = userRepository.getUserInfo(getUserId).getOrElse(new UserInfo())
            val shops = shopRepository.getByHostname(request.host).map(shop => (shop.id.toString, shop.title)).toSeq
            val deliveryInfo = new PickupDeliveryInfo(
              shopId = 0,
              personalInfo = userInfo.personalInfo,
              comment = "",
              register = false)
            val form = pickupForm.fill(deliveryInfo)
            views.html.Order.fillPickup(itemsList, form, shops)
          }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
        else
          Redirect(routes.Cart.display())
  }

  def create() = withUser {
    implicit user =>
      implicit request =>
        deliveryForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(views.html.Order.fillDelivery(cartRepository.list(getUserId), formWithErrors))
          },
          deliveryInfo => {
            val userId =
            if(deliveryInfo.personalInfo.password.isDefined)
              userRepository.register(deliveryInfo.personalInfo.email.get, deliveryInfo.personalInfo.password.get)
            else
              getUserId
            val userInfo = new UserInfo(userId, deliveryInfo.personalInfo, Option(deliveryInfo.address))
            userRepository.updateUserInfo(userInfo)
            val orderId = orderRepository.create(getUserId, deliveryInfo)
            val orderDeliveryInfo = new DeliveryInfo(userInfo.personalInfo.toString(), userInfo.personalInfo.email.getOrElse(""), userInfo.personalInfo.phone, userInfo.address.get.toString())
            emailHelper.orderConfirmation(new OrderInfo(orderId, orderDeliveryInfo, orderRepository.getItems(orderId)))
            Redirect(routes.Order.confirmation(orderId))
          }
        )
  }

  def createPickup() = withUser {
    implicit user =>
      implicit request =>
        pickupForm.bindFromRequest.fold(
          formWithErrors => {
            val shops = shopRepository.getByHostname(request.host).map(shop => (shop.id.toString, shop.title)).toSeq
            BadRequest(views.html.Order.fillPickup(cartRepository.list(getUserId), formWithErrors, shops))
          },
          pickupInfo => {
            val userId =
            if(pickupInfo.personalInfo.password.isDefined)
              userRepository.register(pickupInfo.personalInfo.email.get, pickupInfo.personalInfo.password.get)
            else
              getUserId
            val userInfo = new UserInfo(userId, pickupInfo.personalInfo, None)
            userRepository.updateUserInfo(userInfo)
            val orderId = orderRepository.create(getUserId, pickupInfo)
            val pickupShop = shopRepository.get(pickupInfo.shopId);
            val address = Messages("pickup") + " - " + pickupShop.address;
            val orderDeliveryInfo = new DeliveryInfo(userInfo.personalInfo.toString(), userInfo.personalInfo.email.getOrElse(""), userInfo.personalInfo.phone, address)
            emailHelper.orderConfirmation(new OrderInfo(orderId, orderDeliveryInfo, orderRepository.getItems(orderId)))
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
