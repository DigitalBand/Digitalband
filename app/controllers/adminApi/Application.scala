package controllers.adminApi

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.UserRepository
import helpers.withAdmin

class Application @Inject()(implicit userRepository: UserRepository) extends ControllerBase {
  def index = withAdmin {
    user => request =>
      NotImplemented
  }
}
