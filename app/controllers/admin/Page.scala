package controllers.admin

import com.codahale.jerkson.Json
import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{PageRepository, UserRepository}
import helpers.withAdmin
import models.{PageInfo, CityInfo}
import play.api.Routes

class Page @Inject()
(implicit userRepository: UserRepository, pageRepository: PageRepository) extends ControllerBase {
  def main = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.Page.main())
  }

  def get(pageId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(pageRepository.get(pageId))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def remove(pageId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        pageRepository.remove(pageId)
        Ok("ok")
  }

  def add = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val page = Json.parse[PageInfo](body.toString)
        Ok(Json.generate(pageRepository.add(page))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def update = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val page = Json.parse[PageInfo](body.toString)
        pageRepository.update(page)
        Ok("ok")
  }

  def list = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(pageRepository.list)).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def javascriptRoutes = withAdmin {
    implicit user =>
      implicit request =>
        Ok(
          Routes.javascriptRouter("jsRoutes")(
            controllers.admin.routes.javascript.Page.list,
            controllers.admin.routes.javascript.Page.add,
            controllers.admin.routes.javascript.Page.remove,
            controllers.admin.routes.javascript.Page.update,
            controllers.admin.routes.javascript.Page.get
          )
        ).as("text/javascript")
  }
}