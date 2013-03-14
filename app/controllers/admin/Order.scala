package controllers.admin

import com.google.inject.Inject
import dao.common.{OrderRepository, UserRepository}
import controllers.common.ControllerBase
import play.api.mvc.Action
//TODO: Secure
class Order @Inject()(implicit userRepository: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
  def list = Action {
    implicit request =>
      val orders: Seq[models.OrderInfo] = orderRepository.listAll()
      Ok(views.html.admin.Order.list(orders))
  }

  def display(id: Int) = Action {
    implicit request =>
      val order = orderRepository.get(id)
      Ok(views.html.admin.Order.display(order))
  }
}
