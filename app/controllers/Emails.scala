package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{OrderRepository, UserRepository}
import helpers.withAdmin
import scala.concurrent.ExecutionContext.Implicits.global

class Emails @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
  def orderConfirmation(orderId: Int) = withAdmin { implicit user =>
      implicit request =>
        Ok(views.html.emails.plain.order.notification(orderRepository.get(orderId)))
  }

  def orderAdminConfirmation(orderId: Int) = withAdmin.async { implicit user =>
      implicit request =>
        val cityRepository = db.Global.getControllerInstance(classOf[dao.common.CityRepository])
        cityRepository.getByHostname(request.host).map { city =>
          val order = orderRepository.get(orderId)
          val idMark = city.prefix + order.id.toString
          Ok(views.html.emails.plain.order.adminNotification(order, idMark))
        }

  }
}
