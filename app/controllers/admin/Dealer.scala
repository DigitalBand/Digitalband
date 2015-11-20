package controllers.admin

import com.codahale.jerkson.Json
import com.google.inject.Inject
import controllers.common.ControllerBase
import helpers.withAdmin
import scala.concurrent.ExecutionContext.Implicits.global

class Dealer @Inject()(implicit userRepository: dao.common.UserRepository, dealerRepository: dao.common.DealerRepository) extends ControllerBase {
  def list = withAdmin.async {
    implicit user =>
      implicit request =>
        dealerRepository.list.map { result =>
          val dealers = result.map(i => i.title)
          Ok(Json.generate(dealers)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }
}
