package controllers

import com.google.inject.Inject
import common.ControllerBase
import dao.common.{OrderRepository, UserRepository}
import play.api.mvc.Action
import helpers.Secured

class Emails @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository) extends ControllerBase with Secured {
  def orderConfirmation(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(views.html.emails.plain.order.confirmation(orderRepository.get(orderId)))
  }

  def orderAdminConfirmation(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(views.html.emails.plain.order.adminConfirmation(orderRepository.get(orderId)))

  }
}
