package forms
import play.api.mvc._
import dao.common.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages

object loginForm {
  def apply()(implicit request: Request[AnyContent], userService:UserRepository) = {
    Form(
      tuple(
        "email" -> nonEmptyText,
        "password" -> nonEmptyText
      ) verifying(Messages("validation.login.wronginfo"), (credentials) => {
          val (email, password) = credentials
          val user = userService.authenticate(email, password)
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
          val user = userService.get(email)
          !user.isDefined
      })
    )
  }
}
