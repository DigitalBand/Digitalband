package controllers.admin

import com.google.inject.Inject
import dao.common.{OrderRepository, UserRepository}
import controllers.common.ControllerBase
import play.api.mvc.Action
import models.ListPage

//TODO: Secure
class Order @Inject()(implicit userRepository: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
  def list(pageNumber:Int = 1, pageSize:Int = 10) = Action {
    implicit request =>
      val orders: ListPage[models.OrderInfo] = orderRepository.listAll(pageNumber, pageSize)
      Ok(views.html.admin.Order.list(orders, pageSize, pageNumber))
  }

  def display(id: Int) = Action {
    implicit request =>
      val order = orderRepository.get(id)
      Ok(views.html.admin.Order.display(order))
  }
}
