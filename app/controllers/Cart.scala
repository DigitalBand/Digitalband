package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CartRepository, ProductRepository, UserRepository}
import forms.loginForm
import helpers.withUser
import models.{CItem, CartItem}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

class Cart @Inject()(implicit ur: UserRepository, val cartRepository: CartRepository, productRepository: ProductRepository) extends ControllerBase {
  val addToCartForm = Form(
    mapping(
      "productId" -> number,
      "count" -> number,
      "returnUrl" -> text
    )(CItem.apply)(CItem.unapply)
  )

  def add = withUser {
    implicit user =>
      implicit request =>
        val cItem = addToCartForm.bindFromRequest.get
        val userId = getUserId
        cartRepository.add(new CartItem(userId, cItem.productId, cItem.count))
        Redirect(routes.Cart.display(cItem.returnUrl)) withSession
          request.session + ("userid" -> userId.toString)
  }

  def display(returnUrl: String) = withUser.async {
    implicit user =>
      implicit request =>
        cartRepository.list(getUserId).map { cartItems =>
          Ok(views.html.Cart.display(cartItems, returnUrl))
        }
  }

  def delete(productId: Int, returnUrl: String = "") = withUser {
    implicit user =>
      implicit request =>
        cartRepository.deleteItem(getUserId, productId)
        Redirect(routes.Cart.display(returnUrl))
  }

  def deleteConfirmation(productId: Int, returnUrl: String = "") = withUser.async {
    implicit user =>
      implicit request =>
        for {
          product <- productRepository.get(productId)
        } yield Ok(views.html.Cart.deleteConfirmation(product, returnUrl))
  }

  def update(returnUrl: String) = withUser {
    implicit user =>
      implicit request =>
        val items = getCartItems(request.body)
        cartRepository.updateItems(getUserId, items)
        Redirect(routes.Cart.display(returnUrl))
  }

  def checkout = withUser {
    implicit user =>
      implicit request =>
        if (user.isEmpty)
          Ok(views.html.Cart.checkout(loginForm()))
        else
          Redirect(routes.Order.fill())
  }

  private def getCartItems(body: AnyContent): Seq[CItem] = {
    body.asFormUrlEncoded.get.map {
      case (name: String, its: Seq[String]) =>
        new CItem(its(0).toInt, its(1) match {
          case s if s.matches("[+-]?\\d+") => s.toInt
          case _ => 1
        }, "")
    }.toSeq
  }
}
