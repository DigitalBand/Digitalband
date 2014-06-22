package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{OrderRepository, UserRepository}
import helpers.withAdmin

class Emails @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
  def orderConfirmation(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(views.html.emails.plain.order.confirmation(orderRepository.get(orderId)))
  }

  def orderAdminConfirmation(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(views.html.emails.plain.order.adminConfirmation(orderRepository.get(orderId)))

  }
}
