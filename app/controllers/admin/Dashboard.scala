package controllers.admin

import com.google.inject.Inject
import dao.common.UserRepository
import controllers.common.ControllerBase
import play.api.mvc.Action

//should be secured
class Dashboard @Inject()(implicit userRepository: UserRepository) extends ControllerBase {
  def index = Action {
    implicit request =>
      Ok(views.html.admin.index())
  }
}
