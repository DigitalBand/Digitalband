package controllers.admin

import com.google.inject.Inject
import dao.common.UserRepository
import controllers.common.ControllerBase
import play.api.mvc.Action

class Order @Inject()(implicit userRepository: UserRepository) extends ControllerBase {
  def list = Action {
    implicit request =>
      Ok(views.html.admin.Order.list(List()))
  }
}
