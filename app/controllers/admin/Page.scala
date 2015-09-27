package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{PageRepository, UserRepository}
import helpers.withAdmin
import models.PageInfo
import play.api.Routes
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

class Page @Inject()
(implicit userRepository: UserRepository, pageRepository: PageRepository) extends ControllerBase {
  def main = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.Page.main())
  }

  def get(pageId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        pageRepository.get(pageId).map { page =>
          Ok(Json.toJson(page)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }

  def remove(pageId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        pageRepository.remove(pageId).map { removedCount =>
          Ok("ok")
        }
  }

  def add = withAdmin.async(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val page = Json.parse(body.toString()).validate[PageInfo]
        pageRepository.add(page.get).map { pageId =>
          Ok(Json.toJson(pageId)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }

  def update = withAdmin.async(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val page = Json.parse(body.toString()).validate[PageInfo]
        pageRepository.update(page.get).map { updatedCount =>
          Ok("ok")
        }
  }

  def list = withAdmin.async {
    implicit user =>
      implicit request =>
        pageRepository.list().map { pageList =>
          Ok(Json.toJson(pageList)).withHeaders(CONTENT_TYPE -> "application/json")
        }
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
