package controllers.admin

import com.codahale.jerkson.Json
import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CityRepository, UserRepository}
import helpers.withAdmin
import models.ShopInfo
import play.api.Routes

class City @Inject()(implicit userRepository: UserRepository, cityRepository: CityRepository) extends ControllerBase {
  def main = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.City.main())
  }

  def list = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(cityRepository.list)).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def javascriptRoutes = withAdmin {
    implicit user =>
      implicit request =>
        Ok(
          Routes.javascriptRouter("jsRoutes")(
            controllers.admin.routes.javascript.City.list
//            controllers.admin.routes.javascript.City.add,
//            controllers.admin.routes.javascript.City.remove,
//            controllers.admin.routes.javascript.City.update,
//            controllers.admin.routes.javascript.City.get
          )
        ).as("text/javascript")
  }
}