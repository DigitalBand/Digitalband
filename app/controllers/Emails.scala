package controllers

import com.google.inject.Inject
import common.ControllerBase
import dao.common.UserRepository

class Emails @Inject()(implicit ur: UserRepository) extends ControllerBase {

}
