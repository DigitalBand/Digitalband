package controllers.common

import dao.common.UserRepository
import helpers.SessionHelper
import play.api.mvc._
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class ControllerBase(implicit val userRepository: UserRepository) extends Controller with I18nSupport {

  def getUserId(implicit session: Session): Int = {
    SessionHelper.getUserId(userRepository.createUser, userRepository.getUserId)
  }


  def urlParamToInt(paramName: String)(implicit request: Request[AnyContent]): Int = {
    request.getQueryString(paramName) match {
      case Some(x) => x.toInt
      case None => 0
    }
  }
  //TOD: Implement this
  def isAjax = false

  override def messagesApi: MessagesApi = play.api.i18n.Messages.Implicits.applicationMessagesApi
}
