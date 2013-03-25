package controllers.admin

import com.google.inject.Inject
import dao.common.{OrderRepository, UserRepository}
import controllers.common.ControllerBase
import helpers.Secured
import play.api.libs.json.Json
import play.api.i18n.Messages

class Dashboard @Inject()(implicit userRepository: UserRepository, orderRepository: OrderRepository) extends ControllerBase with Secured {
  def index = withAdmin {
    implicit user =>
      implicit request =>
        val counters = orderRepository.getCounters
        val countersJs = Json.toJson(
          counters.map {
            counter =>
              Json.obj (
                "name" -> Messages("order.list.ui.button." + counter._1 + ".text"),
                "y" -> Json.toJson(counter._2),
                "color" -> Messages("order.statuses.colors." + counter._1)
              )
          })
        Ok(views.html.Admin.index(countersJs))
  }
}
