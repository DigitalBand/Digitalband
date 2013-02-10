package controllers

import play.api.mvc.{Action, Controller}
import com.google.inject.Inject
import dao.common.{UserRepository, CartRepository, OrderRepository}
import helpers.SessionHelper._

class Order @Inject()(orderRepository: OrderRepository, cartRepository: CartRepository, userRepository: UserRepository) extends Controller {
  def fill() = Action {
    implicit request =>
      val cartId = getCartId(session, cartRepository.createCart, userRepository.getUserId)
      Ok(views.html.Order.fill(cartRepository.list(cartId), ""))
  }
}
