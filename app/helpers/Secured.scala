package helpers

import play.api.mvc._
import play.api.mvc.Results._
import play.api.mvc.SimpleResult
import scala.concurrent.Future
import dao.common.UserRepository
import models.UserEntity
import play.api.mvc.BodyParsers.parse
import play.api.Play


class AuthenticatedRequest[A](val username: String, request: Request[A]) extends WrappedRequest[A](request)

object Authenticated extends ActionBuilder[AuthenticatedRequest] {
  lazy val username: String = Play.maybeApplication.flatMap(_.configuration.getString("session.username")) getOrElse ("username")
  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
    request.session.get(username).map {
      username =>
        block(new AuthenticatedRequest(username, request))
    } getOrElse {
      Future.successful(Forbidden)
    }
  }
}


object Security {
  def currentUser = Authenticated {
    request =>
      Ok("The current user is " + request.username)
  }

}

trait Secured {
  def withAdmin(f: Option[UserEntity] => AuthenticatedRequest[AnyContent] => Result)(implicit userRepository: UserRepository):EssentialAction = withAdmin(parse.anyContent)(f)

  def withAdmin[A](bp: BodyParser[A])(f: Option[UserEntity] => AuthenticatedRequest[A] => Result)(implicit userRepository: UserRepository) = Authenticated(bp) {
    request =>
      userRepository.get(request.username) match {
        case Some(user) =>
          if (user.isAdmin) {
            f(Option(user))(request)
          }
          else {
            Forbidden
          }
        case none => Forbidden
      }
  }
  def withUser[A](bp: BodyParser[A])(f: Option[UserEntity] => Request[A] => Future[SimpleResult])(implicit userRepository: UserRepository) = Action.async(bp) {
    implicit request =>
      f(userRepository.get(request.session.get(SessionHelper.username).getOrElse("")))(request)
  }

  def withUser(f: Option[UserEntity] => Request[AnyContent] => Future[SimpleResult])(implicit userRepository: UserRepository): EssentialAction =
    withUser(parse.anyContent)(f)
}
