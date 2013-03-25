package controllers.admin

import com.google.inject.Inject
import dao.common.UserRepository
import controllers.common.ControllerBase


class User @Inject()(implicit userRepository: UserRepository) extends ControllerBase {

}
