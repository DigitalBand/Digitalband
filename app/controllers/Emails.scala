package controllers

import com.google.inject.Inject
import common.ControllerBase
import dao.common.{OrderRepository, UserRepository}
import play.api.mvc.Action

class Emails @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
  def orderConfirmation(orderId: Int) = Action {
     Ok(views.html.emails.order.confirmation(orderRepository.get(orderId)))
  }

  def orderAdminConfirmation(orderId: Int) = Action {
    Ok(views.html.emails.order.adminConfirmation(orderRepository.get(orderId)))
  }
}
