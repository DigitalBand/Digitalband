package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import helpers.Secured
import com.codahale.jerkson.Json
import play.api.Routes
import play.api.mvc.Action

case class StockItemInfo(id: Int, quantity: Int, dealerName: String, dealerPrice: Double)

class StockItem @Inject()(implicit userRepository: dao.common.UserRepository, stockItemRepository: dao.common.StockItemRepository) extends ControllerBase with Secured {
  def edit(productId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.StockItem.edit(productId))
  }

  def list(productId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        Ok(Json.generate(stockItemRepository.list(productId))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def create(productId: Int) = withAdmin(parse.json) {
    implicit user =>
      implicit request =>
        val body = request.body;
        val stockItem = Json.parse[StockItemInfo](body.toString)
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
        val body = request.body;
        val stockItem = Json.parse[StockItemInfo](body.toString)
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
          controllers.admin.routes.javascript.StockItem.update
        )
      ).as("text/javascript")
  }
}