package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import helpers.Secured
import com.codahale.jerkson.Json
import models.DealerInfo

class Dealer @Inject()(implicit userRepository: dao.common.UserRepository, dealerRepository: dao.common.DealerRepository) extends ControllerBase with Secured {
  def list = withAdmin {
    implicit user =>
      implicit request =>
        val dealers: Seq[String] = dealerRepository.list.map(i => i.title)
        Ok(Json.generate(dealers)).withHeaders(CONTENT_TYPE -> "application/json")
  }
}