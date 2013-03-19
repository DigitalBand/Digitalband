package controllers.common

import play.api.mvc._
import dao.common.UserRepository
import scala.Some
import helpers.SessionHelper
import play.api.Play
import models.UserEntity

class ControllerBase(implicit val userRepository: UserRepository) extends Controller {
  def getUserId(implicit session: Session): Int = SessionHelper.getUserId(userRepository.createUser, userRepository.getUserId)

  def withUser(f: Option[UserEntity] => Request[AnyContent] => Result)(implicit userRepository: UserRepository) = Action {
    implicit request =>
      f(userRepository.get(request.session.get(SessionHelper.username).getOrElse("")))(request)
  }
  def urlParamToInt(paramName: String)(implicit request: Request[AnyContent]): Int = {
    request.getQueryString(paramName) match {
      case Some(x) => x.toInt
      case None => 0
    }
  }
}
