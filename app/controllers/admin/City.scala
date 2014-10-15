package controllers.admin

import com.codahale.jerkson.Json
import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CityRepository, UserRepository}
import helpers.withAdmin
import models.CityInfo
import play.api.Routes

class City @Inject()(implicit userRepository: UserRepository, cityRepository: CityRepository) extends ControllerBase {
  def main = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.City.main())
  }

  def get(cityId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(cityRepository.get(cityId))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def remove(cityId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        cityRepository.remove(cityId)
        Ok("ok")
  }

  def add = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val city = Json.parse[CityInfo](body.toString)
        Ok(Json.generate(cityRepository.add(city))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def update = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val city = Json.parse[CityInfo](body.toString)
        cityRepository.update(city)
        Ok("ok")
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
            controllers.admin.routes.javascript.City.list,
            controllers.admin.routes.javascript.City.add,
            controllers.admin.routes.javascript.City.remove,
            controllers.admin.routes.javascript.City.update,
            controllers.admin.routes.javascript.City.get
          )
        ).as("text/javascript")
  }
}