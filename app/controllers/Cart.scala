package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.CartRepository
import models.CartItem

class Cart @Inject()(val cartRepository: CartRepository) extends Controller{

  def add(productId: Int, count: Int = 1) = Action {
    implicit request =>
      val cartId = cartRepository.add(new CartItem(getCartId(session), productId, count))
      Redirect(routes.Cart.display()) withSession
        session + ("cartid" -> cartId.toString)
  }
  def display = Action {
    implicit request =>
    val cartItems: Seq[CartItem] = cartRepository.list(getCartId(session))
    Ok(views.html.Cart.display(cartItems))
  }
  def delete(productId: Int) = Action {
    NotImplemented
  }
  def update = Action {
    NotImplemented
  }
  def getCartId(session: Session) = session.get("cartid") match {
    case Some(x) => x.toInt
    case None => 0
  }
}
