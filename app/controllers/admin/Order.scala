package controllers.admin

import com.google.inject.Inject
import dao.common.{OrderRepository, UserRepository}
import controllers.common.ControllerBase
import play.api.mvc.Action
import models.ListPage
import views.html.admin.Order

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
    NotImplemented
  }

  def cancel(orderId: Int) = Action {
    NotImplemented
  }

  def complete(orderId: Int) = Action {
    NotImplemented
  }

  def delete(orderId: Int) = Action {
    NotImplemented
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
