package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.{UserRepository, ProductRepository, CartRepository}
import models.{CItem, CartItem}
import play.api.data.Form
import play.api.data.Forms._
import helpers.SessionHelper._



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
      val cartId = getCartId(session, cartRepository.createCart, userRepository.getUserId)
      cartRepository.add(new CartItem(cartId, cItem.productId, cItem.count))
      Redirect(routes.Cart.display(cItem.returnUrl)) withSession
        session + ("cartid" -> cartId.toString)
  }

  def display(returnUrl: String) = Action {
    implicit request =>
      val cartId = getCartId(session, cartRepository.createCart, userRepository.getUserId)
      val cartItems: Seq[CartItem] = cartRepository.list(cartId)
      Ok(views.html.Cart.display(cartItems, returnUrl))
  }

  def delete(productId: Int, returnUrl: String = "") = Action {
    implicit request =>
      cartRepository.deleteItem(getCartId(session, cartRepository.createCart, userRepository.getUserId), productId)
      Redirect(routes.Cart.display(returnUrl))
  }

  def deleteConfirmation(productId: Int, returnUrl: String = "") = Action {
    Ok(views.html.Cart.deleteConfirmation(productRepository.get(productId), returnUrl))
  }

  def update(returnUrl: String) = Action {
    implicit request =>
      val items = getCartItems(request.body)
      val cartId = getCartId(session, cartRepository.createCart, userRepository.getUserId)
      cartRepository.updateItems(cartId, items)
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




}
