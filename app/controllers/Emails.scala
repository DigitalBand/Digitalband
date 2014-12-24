package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{OrderRepository, UserRepository}
import helpers.withAdmin

class Emails @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
  def orderConfirmation(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(views.html.emails.plain.order.notification(orderRepository.get(orderId)))
  }

  def orderAdminConfirmation(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        val cityRepository = db.Global.getControllerInstance(classOf[dao.common.CityRepository])
        val city = cityRepository.getByHostname(request.host)
        val order = orderRepository.get(orderId)
        val idMark = city.prefix + order.id.toString
        Ok(views.html.emails.plain.order.adminNotification(order, idMark))

  }
}
