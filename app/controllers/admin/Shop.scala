package controllers.admin

import com.codahale.jerkson.Json
import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{ShopRepository, UserRepository}
import helpers.withAdmin
import models.ShopInfo
import play.api.Routes

class Shop @Inject()(implicit userRepository: UserRepository, shopRepository: ShopRepository) extends ControllerBase {
  def main = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.Shop.main())
  }

  def get(shopId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(shopRepository.get(shopId))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def getByCity(cityId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(shopRepository.getByCity(cityId))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def remove(shopId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        shopRepository.remove(shopId)
        Ok("ok")
  }

  def add = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val shop = Json.parse[ShopInfo](body.toString)
        Ok(Json.generate(shopRepository.add(shop))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def update = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val shop = Json.parse[ShopInfo](body.toString)
        shopRepository.update(shop)
        Ok("ok")
  }



  def list = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(shopRepository.list)).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def javascriptRoutes = withAdmin {
    implicit user =>
      implicit request =>
        Ok(
          Routes.javascriptRouter("jsRoutes")(
            controllers.admin.routes.javascript.Shop.list,
            controllers.admin.routes.javascript.Shop.add,
            controllers.admin.routes.javascript.Shop.remove,
            controllers.admin.routes.javascript.Shop.update,
            controllers.admin.routes.javascript.Shop.get,
            controllers.admin.routes.javascript.City.listShortInfo
          )
        ).as("text/javascript")
  }
}
