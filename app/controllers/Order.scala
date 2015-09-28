package controllers

import models._
import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CartRepository, OrderRepository, UserRepository, ShopRepository, CityRepository}
import helpers.{EmailHelper, withUser}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class Order @Inject()(implicit ur: UserRepository,
                               orderRepository: OrderRepository,
                               cartRepository: CartRepository,
                               shopRepository: ShopRepository,
                               cityRepository: CityRepository) extends ControllerBase {
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
      "email" -> optional(email)
    )(PersonalInfo.apply)(PersonalInfo.unapply),
    "comment" -> text
  )(OrderDeliveryInfo.apply)(OrderDeliveryInfo.unapply))

  val pickupForm = Form(mapping(
    "shopId" -> number,
    "personalInfo" -> mapping(
      "lastName" -> nonEmptyText(minLength = 2, maxLength = 50),
      "firstName" -> nonEmptyText(minLength = 2, maxLength = 50),
      "middleName" -> text,
      "phone" -> nonEmptyText(minLength = 10, maxLength = 25),
      "email" -> optional(email)
    )(PersonalInfo.apply)(PersonalInfo.unapply),
    "comment" -> text
  )(PickupDeliveryInfo.apply)(PickupDeliveryInfo.unapply))

  val emailHelper = new EmailHelper()

  def fill = withUser.async {
    implicit user =>
      implicit request =>
        cartRepository.list(getUserId).map { itemsList =>
          if (itemsList.nonEmpty)
            Ok {
              views.html.Order.fill(itemsList)
            }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
          else
            Redirect(routes.Cart.display())
        }
  }

  def fillDelivery() = withUser.async {
    implicit user =>
      implicit request =>
        cartRepository.list(getUserId).map { itemsList =>
          if (itemsList.nonEmpty)
            Ok {
              val userInfo = userRepository.getUserInfo(getUserId).getOrElse(new UserInfo())
              val deliveryInfo = new OrderDeliveryInfo(address = userInfo.address.get, personalInfo = userInfo.personalInfo, comment = "")
              val form = deliveryForm.fill(deliveryInfo)
              views.html.Order.fillDelivery(itemsList, form)

            }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
          else
            Redirect(routes.Cart.display())
        }
  }

  def fillPickup = withUser.async {
    implicit user =>
      implicit request =>
        for {
          itemsList <- cartRepository.list(getUserId)
          shopList <- shopRepository.getByHostname(request.host)
        } yield  {
          if (itemsList.nonEmpty)
            Ok {
              val userInfo = userRepository.getUserInfo(getUserId).getOrElse(new UserInfo())
              val shops = shopList.map(shop => (shop.id.toString, shop.title))
              val deliveryInfo = new PickupDeliveryInfo(shopId = 0, personalInfo = userInfo.personalInfo, comment = "")
              val form = pickupForm.fill(deliveryInfo)
              views.html.Order.fillPickup(itemsList, form, shops)
            }.withHeaders(CACHE_CONTROL -> "no-cache, max-age=0, must-revalidate, no-store")
          else
            Redirect(routes.Cart.display())
        }
  }

  def create() = withUser.async {
    implicit user =>
      implicit request =>
        deliveryForm.bindFromRequest.fold(
          formWithErrors => cartRepository.list(getUserId).map { cartItems =>
            BadRequest(views.html.Order.fillDelivery(cartItems, formWithErrors))
          },
          deliveryInfo => {

            val userInfo = new UserInfo(getUserId, deliveryInfo.personalInfo, Option(deliveryInfo.address))
            userRepository.updateUserInfo(userInfo)

            val orderDeliveryInfo = new DeliveryInfo(userInfo.personalInfo.toString(),
              userInfo.personalInfo.email.getOrElse(""),
              userInfo.personalInfo.phone, userInfo.address.get.toString())
            for {
              city <- cityRepository.getByHostname(request.host)
              orderId <- orderRepository.create(getUserId, cityId(city), deliveryInfo)
              orderItems <- orderRepository.getItems(orderId)
            } yield {
              emailHelper.orderConfirmation(new OrderInfo(orderId, orderDeliveryInfo, orderItems))
              Redirect(routes.Order.confirmation(orderId))
            }
          }
        )
  }

  private def cityId(city: CityInfo) = if (city != null) Option(city.id) else None

  def createPickup() = withUser.async {
    implicit user =>
      implicit request =>
        pickupForm.bindFromRequest.fold(
          formWithErrors => for {
            cartItems <- cartRepository.list(getUserId)
            shops <- shopRepository.getByHostname(request.host)
          } yield {
            BadRequest(views.html.Order.fillPickup(
              cartItems,
              formWithErrors,
              shops.map(shop => (shop.id.toString, shop.title))))
          },
          pickupInfo => {
            val userInfo = new UserInfo(getUserId, pickupInfo.personalInfo, None)
            userRepository.updateUserInfo(userInfo)
            for {
              city <- cityRepository.getByHostname(request.host)
              orderId <- orderRepository.create(getUserId, cityId(city), pickupInfo)
              orderItems <- orderRepository.getItems(orderId)
              pickupShop <- shopRepository.get(pickupInfo.shopId)
            } yield {
              val address = Messages("pickup") + " - " + pickupShop.address
              val orderDeliveryInfo = new DeliveryInfo(userInfo.personalInfo.toString(),
                userInfo.personalInfo.email.getOrElse(""),
                userInfo.personalInfo.phone, address)
              emailHelper.orderConfirmation(new OrderInfo(orderId, orderDeliveryInfo, orderItems))
              Redirect(routes.Order.confirmation(orderId))
            }
          }
        )
  }

  def confirmation(orderId: Int) = withUser.async {
    implicit user => implicit request =>
      orderRepository.getItems(orderId).map {
        orderItems =>
          Ok(views.html.Order.confirmation(orderItems, orderId))
      }
  }

  def display(orderId: Int) = withUser {
    implicit user =>
      implicit request =>
        NotImplemented
  }
}
