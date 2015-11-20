package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CityRepository, ShopRepository, UserRepository}
import helpers.withAdmin
import models.CityInfo
import play.api.Routes
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

class City @Inject()
(implicit userRepository: UserRepository,
 cityRepository: CityRepository,
 shopRepository: ShopRepository) extends ControllerBase {
  def main = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.City.main())
  }

  def get(cityId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        cityRepository.get(cityId).map {
          cityInfo =>
            Ok(Json.toJson(cityInfo)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }

  def remove(cityId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        cityRepository.remove(cityId)
        Ok("ok")
  }

  def add = withAdmin.async(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val city = Json.parse(body.toString()).validate[CityInfo]
        cityRepository.add(city.get).map {
          cityId =>
            Ok(Json.toJson(cityId)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }

  def update = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val city = Json.parse(body.toString()).validate[CityInfo]
        cityRepository.update(city.get)
        Ok("ok")

  }

  def list = withAdmin.async {
    implicit user =>
      implicit request =>
        cityRepository.list.map {
          cityList =>
            Ok(Json.toJson(cityList)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }

  def listShortInfo = withAdmin.async {
    implicit user =>
      implicit request =>
        cityRepository.listShortInfo.map {
          cityList =>
            Ok(Json.toJson(cityList)).withHeaders(CONTENT_TYPE -> "application/json")
        }
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
            controllers.admin.routes.javascript.City.get,
            controllers.admin.routes.javascript.Shop.getByCity,
            controllers.admin.routes.javascript.Shop.get,
            controllers.admin.routes.javascript.Shop.add,
            controllers.admin.routes.javascript.Shop.remove,
            controllers.admin.routes.javascript.Shop.update,
            controllers.admin.routes.javascript.City.listShortInfo
          )
        ).as("text/javascript")
  }
}
