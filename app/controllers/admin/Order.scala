package controllers.admin

import com.google.inject.Inject
import dao.common.{OrderRepository, UserRepository}
import controllers.common.ControllerBase
import play.api.mvc.Action
import models.ListPage
import views.html.admin.Order

import play.api.data._
import play.api.data.Forms._
import helpers.EmailHelper

//TODO: Secure
class Order @Inject()(implicit userRepository: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
  val orderStatusForm = Form("comment" -> text)
  val emailHelper = new EmailHelper()

  def list(pageNumber: Int = 1, pageSize: Int = 10) = Action {
    implicit request =>
      val orders: ListPage[models.OrderInfo] = orderRepository.listAll(pageNumber, pageSize)
      Ok(Order.list(orders, pageSize, pageNumber))
  }

  def display(id: Int) = Action {
    implicit request =>
      val order = orderRepository.get(id)
      Ok(Order.display(order))
  }

  def confirm(orderId: Int) = Action {
    implicit request =>
      val comment = orderStatusForm.bindFromRequest().get
      orderRepository.changeStatus(orderId, "confirmed")
      emailHelper.orderConfirmed(comment, orderRepository.get(orderId))
      Redirect(controllers.admin.routes.Order.display(orderId))
  }

  def cancel(orderId: Int) = Action {
    implicit request =>
      val comment = orderStatusForm.bindFromRequest().get
      orderRepository.changeStatus(orderId, "canceled")
      emailHelper.orderCanceled(comment, orderRepository.get(orderId))
      Redirect(controllers.admin.routes.Order.display(orderId))
  }

  def complete(orderId: Int) = Action {
    orderRepository.changeStatus(orderId, "done")
    Redirect(controllers.admin.routes.Order.display(orderId))
  }

  def delete(orderId: Int) = Action {
    orderRepository.delete(orderId)
    Redirect(controllers.admin.routes.Order.list(1))
  }

  def confirmPage(orderId: Int) = Action {
    implicit request =>
      Ok(Order.confirmPage(orderRepository.get(orderId)))
  }

  def cancelPage(orderId: Int) = Action {
    implicit request =>
      Ok(Order.cancelPage(orderRepository.get(orderId)))
  }

  def completePage(orderId: Int) = Action {
    implicit request =>
      Ok(Order.completePage(orderId))
  }

  def deletePage(orderId: Int) = Action {
    implicit request =>
      Ok(Order.deletePage(orderId))
  }
}
