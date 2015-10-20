package forms
import play.api.mvc._
import dao.common.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, Messages}
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.i18n.Messages.Implicits._

object loginForm {
  def apply()(implicit request: Request[AnyContent], userService:UserRepository, messages: Messages) = {
    Form(
      tuple(
        "email" -> nonEmptyText,
        "password" -> nonEmptyText
      ) verifying(Messages("validation.login.wronginfo"), (credentials) => {
          val (email, password) = credentials
          //TODO: Fix the await
          val user = Await.result(userService.authenticate(email, password), Duration(2, SECONDS))
          user.isDefined
      })
    )
  }
}

object registrationForm {
  def apply()(implicit request: Request[AnyContent], userService:UserRepository, messages: Messages) = {
    Form(
      tuple(
        "email" -> nonEmptyText,
        "password" -> nonEmptyText
      ) verifying(Messages("validation.registration.emailexists"), (credentials) => {
          val (email, password) = credentials
          //TODO: Fix the await
          val user = Await.result(userService.get(email), Duration(2, SECONDS))
          user.isEmpty
      })
    )
  }
}
