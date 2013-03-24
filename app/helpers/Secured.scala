package helpers

import dao.common.UserRepository
import play.api.mvc._

import models.UserEntity
import play.api.Play
import play.api.mvc.BodyParsers._
import forms.loginForm
import controllers.routes
object Security {
  import play.api.mvc.Results._
  import play.api.libs.iteratee._
  def Authenticated[A](userinfo: RequestHeader => Option[A],
                        onUnauthorized: RequestHeader => Result)(action: A => EssentialAction): EssentialAction = {
    EssentialAction { request =>
      userinfo(request).map { user =>
        action(user)(request)
      }.getOrElse {
        Done(onUnauthorized(request), Input.Empty)
      }
    }

  }
  lazy val username: String = Play.maybeApplication.flatMap(_.configuration.getString("session.username")) getOrElse ("username")
  def Authenticated(action: String => EssentialAction): EssentialAction = Authenticated(
    req => req.session.get(username),
    request => Results.Redirect(routes.Security.login(request.uri)))(action)

}
trait Secured {
  import Security._
  def withAdmin[A](bp: BodyParser[A])(f: Option[UserEntity] => Request[A] => Result)(implicit userRepository: UserRepository) = Authenticated {
    username => Action(bp) {
      implicit request =>
        userRepository.get(username).map { user =>
          if(user.isAdmin)
            f(Some(user))(request)
          else
            Results.Redirect(routes.Security.login(request.uri)).flashing("alert-error" -> "security.alerts.admin.auth.denied")
        }.getOrElse(Results.Redirect(routes.Security.login(request.uri)))
    }
  }

  def withAdmin(f: Option[UserEntity] => Request[AnyContent] => Result)(implicit userRepository: UserRepository): EssentialAction =
    withAdmin(parse.anyContent)(f)
}
