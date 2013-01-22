package controllers.common

import play.api.mvc.{AnyContent, Request, Controller}

class ControllerBase extends Controller{
  def intParam(paramName: String)(implicit request: Request[AnyContent]): Int = {
    request.getQueryString(paramName) match {
      case Some(x) => x.toInt
      case None => 0
    }
  }
}
