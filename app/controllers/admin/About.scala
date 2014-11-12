package controllers.admin

import com.codahale.jerkson.Json
import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{AboutRepository, UserRepository}
import helpers.withAdmin
import models.{AboutInfo}
import play.api.Routes

class About @Inject()(implicit userRepository: UserRepository, aboutRepository: AboutRepository) extends ControllerBase {
  def main = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.About.main())
  }

  def get() = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(aboutRepository.get())).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def save = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val aboutInfo = Json.parse[AboutInfo](body.toString)
        Ok(Json.generate(aboutRepository.save(aboutInfo))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def javascriptRoutes = withAdmin {
    implicit user =>
      implicit request =>
        Ok(
          Routes.javascriptRouter("jsRoutes")(
            controllers.admin.routes.javascript.About.save,
            controllers.admin.routes.javascript.About.get
          )
        ).as("text/javascript")
  }
}