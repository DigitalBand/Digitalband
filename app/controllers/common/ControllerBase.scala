package controllers.common

import play.api.mvc.{RequestHeader, AnyContent, Request, Controller}
import dao.common.UserRepository

class ControllerBase(val userRepository:UserRepository) extends Controller {
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
