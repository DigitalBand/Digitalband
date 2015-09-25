package controllers.admin

import com.google.inject.Inject
import dao.common.{QuestionRepository, ProductRepository, OrderRepository, UserRepository}
import controllers.common.ControllerBase
import helpers.withAdmin
import play.api.libs.json.Json
import play.api.i18n.Messages
import scala.concurrent.ExecutionContext.Implicits.global

class Dashboard @Inject()(implicit userRepository: UserRepository, orderRepository: OrderRepository, productRepository: ProductRepository, questionRepository: QuestionRepository) extends ControllerBase {
  def index = withAdmin.async {
    implicit user =>
      implicit request =>
        for {
          counters <- orderRepository.getCounters
        } yield {
          val questions = questionRepository.listUnanswered()
          val countersJs = Json.toJson(
            counters.map {
              counter =>
                Json.obj(
                  "name" -> Messages("order.list.ui.button." + counter._1 + ".text"),
                  "y" -> Json.toJson(counter._2),
                  "color" -> Messages("order.statuses.colors." + counter._1)
                )
            })
          Ok(views.html.Admin.index(countersJs, questions))
        }
  }
}
