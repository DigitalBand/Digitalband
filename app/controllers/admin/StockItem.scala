package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import helpers.withAdmin
import com.codahale.jerkson.Json
import play.api.Routes
import play.api.mvc.Action
import models.StockItemInfo


class StockItem @Inject()(
                           implicit userRepository: dao.common.UserRepository,
                           stockItemRepository: dao.common.StockItemRepository,
                           productRepository: dao.common.ProductRepository) extends ControllerBase {
  def edit(productId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        val product = productRepository.get(productId)
        Ok(views.html.Admin.StockItem.edit(product))
  }

  def list(productId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(stockItemRepository.list(productId))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def create(productId: Int) = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body
        val stockItem = Json.parse[StockItemInfo](body.toString())
        val id = stockItemRepository.create(productId, stockItem)
        Ok(Json.generate(id))
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
        val stockItem = Json.parse[StockItemInfo](body.toString())
        stockItemRepository.update(stockItem)
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