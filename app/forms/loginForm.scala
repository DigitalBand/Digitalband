package forms

/**
 * Created with IntelliJ IDEA.
 * User: TTkachenko
 * Date: 28.02.13
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
object loginForm {
  def apply(userService: UserRepository)(implicit request: Request[AnyContent]) = {
    Form(
      tuple(
        "email" -> nonEmptyText,
        "password" -> nonEmptyText
      ) verifying(Messages("validation.login.wronginfo"), result =>
        result match {
          case (email, password) =>
            userService.authenticate(email, password).isDefined
        })
    )
  }
}
