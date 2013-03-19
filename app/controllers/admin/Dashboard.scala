package controllers.admin

import com.google.inject.Inject
import dao.common.UserRepository
import controllers.common.ControllerBase
import play.api.mvc.Action
import helpers.Secured

//should be secured
class Dashboard @Inject()(implicit userRepository: UserRepository) extends ControllerBase with Secured{
  def index = withAdmin { implicit user =>
    implicit request =>
      Ok(views.html.admin.index())
  }
}
