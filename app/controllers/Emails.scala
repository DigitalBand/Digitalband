package controllers

import com.google.inject.Inject
import common.ControllerBase
import dao.common.{OrderRepository, UserRepository}
import play.api.mvc.Action

class Emails @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
  def orderConfirmation(orderId: Int) = withUser { implicit user =>
      implicit request =>
        Ok(views.html.emails.plain.order.confirmation(orderRepository.get(orderId))).
          withHeaders(CONTENT_TYPE -> "text/plain")
  }

  def orderAdminConfirmation(orderId: Int) = withUser { implicit user =>
      implicit request =>
        Ok(views.html.emails.plain.order.adminConfirmation(orderRepository.get(orderId))).
          withHeaders(CONTENT_TYPE -> "text/plain")
  }
}
