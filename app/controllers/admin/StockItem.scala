package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import helpers.withAdmin
import play.api.libs.json._
import play.api.Routes
import play.api.mvc.Action
import models.StockItemInfo
import scala.concurrent.ExecutionContext.Implicits.global

class StockItem @Inject()(
                           implicit userRepository: dao.common.UserRepository,
                           stockItemRepository: dao.common.StockItemRepository,
                           productRepository: dao.common.ProductRepository) extends ControllerBase {
  def edit(productId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        for {
          product <- productRepository.get(productId)
        } yield Ok(views.html.Admin.StockItem.edit(product))
  }

  def list(productId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        for {
          stockItems <- stockItemRepository.list(productId)
        } yield {
          Ok(Json.toJson(stockItems)).withHeaders(CONTENT_TYPE -> "application/json")
        }
  }

  def create(productId: Int) = withAdmin.async(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val stockItem = Json.parse(body.toString()).validate[StockItemInfo]
        for {
          id <- stockItemRepository.create(productId, stockItem.get)
        } yield Ok(Json.toJson(id))
  }

  def remove(id: Int) = withAdmin {
    implicit user =>
      implicit request =>
        stockItemRepository.remove(id)
        Ok("ok")
  }

  def update(id: Int) = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val stockItem = Json.parse(body.toString()).validate[StockItemInfo]
        stockItemRepository.update(stockItem.get)
        Ok("Ok")
  }

  def javascriptRoutes = Action {
    implicit request =>
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          controllers.admin.routes.javascript.StockItem.list,
          controllers.admin.routes.javascript.StockItem.create,
          controllers.admin.routes.javascript.StockItem.remove,
          controllers.admin.routes.javascript.StockItem.update,
          controllers.admin.routes.javascript.Dealer.list,
          controllers.admin.routes.javascript.Shop.list
        )
      ).as("text/javascript")
  }
}
