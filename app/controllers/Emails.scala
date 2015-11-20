package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{OrderRepository, UserRepository}
import helpers.withAdmin
import models.{CityInfo, OrderInfo}
import scala.concurrent.ExecutionContext.Implicits.global

class Emails @Inject()(implicit ur: UserRepository, orderRepository: OrderRepository) extends ControllerBase {
  def orderConfirmation(orderId: Int) = withAdmin.async { implicit user =>
    implicit request => orderRepository.get(orderId).map { order =>
      Ok(views.html.emails.plain.order.notification(order))
    }
  }

  def orderAdminConfirmation(orderId: Int) = withAdmin.async { implicit user =>
    implicit request =>
      val cityRepository = db.Global.getControllerInstance(classOf[dao.common.CityRepository])
      for {
        city <- cityRepository.getByHostname(request.host)
        order <- orderRepository.get(orderId)
      } yield Ok(views.html.emails.plain.order.adminNotification(order, idMark(order, city)))
  }

  private def idMark(order: OrderInfo, city: CityInfo) = city.prefix + order.id.toString
}
