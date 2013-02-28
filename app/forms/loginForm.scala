package forms
import play.api.mvc._
import dao.common.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages

object loginForm {
  def apply(userService: UserRepository)(implicit request: Request[AnyContent]) = {
    Form(
      tuple(
        "email" -> nonEmptyText,
        "password" -> nonEmptyText
      ) verifying(Messages("validation.login.wronginfo"), result =>
        result match {
          case (email, password) =>
            val user = userService.authenticate(email, password)
            user.isDefined
        })
    )
  }
}
