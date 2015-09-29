package forms
import play.api.mvc._
import dao.common.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import scala.concurrent.Await
import scala.concurrent.duration._

object loginForm {
  def apply()(implicit request: Request[AnyContent], userService:UserRepository) = {
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
  def apply()(implicit request: Request[AnyContent], userService:UserRepository) = {
    Form(
      tuple(
        "email" -> nonEmptyText,
        "password" -> nonEmptyText
      ) verifying(Messages("validation.registration.emailexists"), (credentials) => {
          val (email, password) = credentials
          //TODO: Fix the await
          val user = Await.result(userService.get(email), Duration(2, SECONDS))
          !user.isDefined
      })
    )
  }
}
