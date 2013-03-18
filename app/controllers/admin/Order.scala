package controllers.admin

import com.google.inject.Inject
import dao.common.{OrderRepository, UserRepository}
import controllers.common.ControllerBase
import play.api.mvc.Action
import models.ListPage
import views.html.admin.Order
import helpers.EmailHelper

//TODO: Secure
class Order @Inject()(implicit userRepository: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
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
    orderRepository.changeStatus(orderId, "confirmed")
    //TODO: send email to user
    //EmailHelper.orderConfirmed(message)
    Redirect(controllers.admin.routes.Order.display(orderId))
  }

  def cancel(orderId: Int) = Action {
    orderRepository.changeStatus(orderId, "canceled")
    //TODO: send email to user
    //EmailHelper.orderCanceled(message)
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
      Ok(Order.confirmPage(orderId))
  }

  def cancelPage(orderId: Int) = Action {
    implicit request =>
      Ok(Order.cancelPage(orderId))
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
