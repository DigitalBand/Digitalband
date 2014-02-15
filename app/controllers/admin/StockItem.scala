package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import helpers.Secured
import com.codahale.jerkson.Json
import play.api.Routes
import play.api.mvc.Action

case class StockItemInfo(quantity: Int, dealerName: String, dealerPrice: Double)

class StockItem @Inject()(implicit userRepository: dao.common.UserRepository) extends ControllerBase with Secured {
  def edit(productId: Int) = withAdmin {
    implicit user =>
      implicit request =>
      Ok(views.html.Admin.StockItem.edit(productId))
  }

  def list(productId: Int) = withAdmin {
    implicit user =>
      implicit request =>
      Ok(Json.generate(List(StockItemInfo(1, "Музторг", 4500),StockItemInfo(1, "Слами", 56000)))).withHeaders(CONTENT_TYPE -> "application/json")
  }

  def javascriptRoutes = Action {
    implicit request =>
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          controllers.admin.routes.javascript.StockItem.list
        )
      ).as("text/javascript")
  }
}