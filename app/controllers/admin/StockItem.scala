package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import helpers.Secured

class StockItem @Inject()(implicit userRepository: dao.common.UserRepository) extends ControllerBase with Secured {
  def edit(productId: Int) = withAdmin {
    implicit user =>
      implicit request =>
      Ok(views.html.Admin.StockItem.edit())
  }
}