package controllers

import com.google.inject.Inject
import dao.common.UserRepository
import controllers.common.ControllerBase
import play.api.mvc.Action

//should be secured
class Admin @Inject()(implicit userRepository: UserRepository) extends ControllerBase {
  def index = Action {
    implicit request =>
      Ok(views.html.Admin.index())
  }

  def orders = Action {
    implicit request =>
    Ok(views.html.Admin.Order.list(List()))
  }
}
