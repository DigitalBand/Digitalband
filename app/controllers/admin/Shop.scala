package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{ShopRepository, UserRepository}
import helpers.withAdmin
import models.ShopInfo
import scala.concurrent.ExecutionContext.Implicits.global
import controllers.admin.{routes => r}
import play.api.libs.json.Json

import play.api.routing.JavaScriptReverseRouter

class Shop @Inject()(implicit userRepository: UserRepository, shopRepository: ShopRepository) extends ControllerBase {
  def main = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.Shop.main())
  }

  def get(shopId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        shopRepository.get(shopId).map {
          shopInfo =>
            Ok(Json.toJson(shopInfo)).withHeaders(CONTENT_TYPE -> "application/json")
        }

  }

  def getByCity(cityId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        shopRepository.getByCity(cityId).map {
          shopInfo =>
            Ok(Json.toJson(shopInfo)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }

  def remove(shopId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        shopRepository.remove(shopId)
        Ok("ok")
  }

  def add = withAdmin.async(parse.json) {
    implicit user =>
      implicit request =>
        val shop = request.body.as[ShopInfo]
        shopRepository.add(shop).map {
          id =>
            Ok(Json.toJson(id)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }

  def update = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val shop = request.body.as[ShopInfo]
        shopRepository.update(shop)
        Ok("ok")
  }

  def list = withAdmin.async {
    implicit user =>
      implicit request =>
        shopRepository.list.map {
          shops =>
            Ok(Json.toJson(shops)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }

  def javascriptRoutes = withAdmin {
    implicit user =>
      implicit request =>
        Ok(
          JavaScriptReverseRouter("jsRoutes")(
            r.javascript.Shop.list,
            r.javascript.Shop.add,
            r.javascript.Shop.remove,
            r.javascript.Shop.update,
            r.javascript.Shop.get,
            r.javascript.City.listShortInfo
          )
        ).as("text/javascript")
  }
}
