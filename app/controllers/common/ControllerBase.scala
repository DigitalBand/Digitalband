package controllers.common

import play.api.mvc._
import dao.common.UserRepository
import scala.Some
import helpers.SessionHelper

class ControllerBase(val userRepository:UserRepository) extends Controller {
  def userId(implicit session: Session): Int = SessionHelper.getUserId(userRepository.createUser, userRepository.getUserId)
  implicit def user(implicit request: RequestHeader) = {
    userRepository.get(request.session.get("email").getOrElse(""))
  }
  def intParam(paramName: String)(implicit request: Request[AnyContent]): Int = {
    request.getQueryString(paramName) match {
      case Some(x) => x.toInt
      case None => 0
    }
  }
}
