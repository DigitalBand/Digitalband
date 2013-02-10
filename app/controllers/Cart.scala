package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.{UserRepository, ProductRepository, CartRepository}
import models.{CItem, CartItem}
import play.api.data.Form
import play.api.data.Forms._
import scala.Some



class Cart @Inject()(val cartRepository: CartRepository, productRepository: ProductRepository, userRepository: UserRepository) extends Controller {
  val addToCartForm = Form(
    mapping(
      "productId" -> number,
      "count" -> number,
      "returnUrl" -> text
    )(CItem.apply)(CItem.unapply)
  )

  def add = Action {
    implicit request =>
      val cItem = addToCartForm.bindFromRequest.get
      val cartId = cartRepository.add(new CartItem(getCartId(session), cItem.productId, cItem.count))
      Redirect(routes.Cart.display(cItem.returnUrl)) withSession
        session + ("cartid" -> cartId.toString)
  }

  def display(returnUrl: String) = Action {
    implicit request =>
      val cartItems: Seq[CartItem] = cartRepository.list(getCartId(session))
      Ok(views.html.Cart.display(cartItems, returnUrl))
  }

  def delete(productId: Int, returnUrl: String = "") = Action {
    implicit request =>
      cartRepository.deleteItem(getCartId(session), productId)
      Redirect(routes.Cart.display(returnUrl))
  }

  def deleteConfirmation(productId: Int, returnUrl: String = "") = Action {
    Ok(views.html.Cart.deleteConfirmation(productRepository.get(productId), returnUrl))
  }

  def update(returnUrl: String) = Action {
    implicit request =>
      val items = getCartItems(request.body)
      cartRepository.updateItems(getCartId(session), items)
      Redirect(routes.Cart.display(returnUrl))
  }

  private def getCartItems(body: AnyContent): Seq[CItem] = {
    body.asFormUrlEncoded.get.map {
      case (name: String, its: Seq[String]) =>
        new CItem(its(0).toInt, its(1) match {
          case s if s.matches("[+-]?\\d+")  => s.toInt
          case _ => 1
        }, "")
    }.toSeq
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
