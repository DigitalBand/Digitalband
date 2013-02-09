package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.{UserRepository, ProductRepository, CartRepository}
import models.CartItem
import play.api.data.Form
import play.api.data.Forms._
import scala.Some

case class CItem(productId: Int, count: Int)

class Cart @Inject()(val cartRepository: CartRepository, productRepository: ProductRepository, userRepository: UserRepository) extends Controller {
  val addToCartForm = Form(
    mapping(
      "productId" -> number,
      "count" -> number
    )(CItem.apply)(CItem.unapply)
  )

  def add = Action {
    implicit request =>
      val cItem = addToCartForm.bindFromRequest.get
      val cartId = cartRepository.add(new CartItem(getCartId(session), cItem.productId, cItem.count))
      Redirect(routes.Cart.display()) withSession
        session + ("cartid" -> cartId.toString)
  }

  def display = Action {
    implicit request =>
      val cartItems: Seq[CartItem] = cartRepository.list(getCartId(session))
      Ok(views.html.Cart.display(cartItems))
  }

  def delete(productId: Int) = Action {
    implicit request =>
      cartRepository.deleteItem(getCartId(session), productId)
      Redirect(routes.Cart.display)
  }

  def deleteConfirmation(productId: Int) = Action {
    Ok(views.html.Cart.deleteConfirmation(productRepository.get(productId)))
  }

  def update = Action {
    NotImplemented
  }

  def getCartId(session: Session) = session.get("cartid") match {
    case Some(x) => x.toInt
    case None => cartRepository.createCart(session.get("username") match {
      case None => 0
      case Some(x) => getUserId(x)
    })
  }

  def getUserId(userName: String) = {
    userRepository.get(userName) match {
      case None => 0
      case Some(x) => x.id
    }
  }
}
