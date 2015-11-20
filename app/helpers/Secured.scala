package helpers

import dao.common.UserRepository
import models.UserEntity
import play.api.mvc.BodyParsers.parse
import play.api.mvc.Results._
import play.api.mvc._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class AuthenticatedRequest[A](val username: String, request: Request[A]) extends WrappedRequest[A](request)

object Authenticated {

  def async[A](bp: BodyParser[A])(block: (AuthenticatedRequest[A]) => Future[Result]) = Action.async(bp) {
    request =>
      request.session.get(SessionHelper.username).map {
        username =>
          block(new AuthenticatedRequest(username, request))
      } getOrElse {
        Future.successful(Forbidden)
      }
  }

  def async[A](block: (AuthenticatedRequest[AnyContent]) => Future[Result]): EssentialAction = async(parse.anyContent)(block)

  def apply[A](block: (AuthenticatedRequest[AnyContent]) => Result): EssentialAction = apply(parse.anyContent)(block)

  def apply[A](bp: BodyParser[A])(block: (AuthenticatedRequest[A]) => Result) = Action(bp) {
    request =>
      request.session.get(SessionHelper.username).map {
        username =>
          block(new AuthenticatedRequest(username, request))
      } getOrElse {
        Forbidden
      }
  }
}

object withUser {
  def apply[A](bp: BodyParser[A])(f: Option[UserEntity] => Request[A] => Result)(implicit userRepository: UserRepository): EssentialAction = Action(bp) {
    request =>
      //TODO: fix the await
      val user = Await.result(userRepository.get(request.session.get(SessionHelper.username).getOrElse("")), Duration(2, SECONDS))
      f(user)(request)
  }
  def apply[A](f: Option[UserEntity] => Request[AnyContent] => Result)(implicit userRepository: UserRepository): EssentialAction =  {
      apply(parse.anyContent)(f)
  }
  def async[A](bp: BodyParser[A])(f: Option[UserEntity] => Request[A] => Future[Result])(implicit userRepository: UserRepository) = Action.async(bp) {
    request =>
      userRepository.get(request.session.get(SessionHelper.username).getOrElse("")).flatMap {
        user =>
          f(user)(request)
      }

  }
  def async[A](f: Option[UserEntity] => Request[AnyContent] => Future[Result])(implicit userRepository: UserRepository): EssentialAction =
    async(parse.anyContent)(f)
}

object withAdmin {
  def apply(f: Option[UserEntity] => AuthenticatedRequest[AnyContent] => Result)(implicit userRepository: UserRepository):EssentialAction = apply(parse.anyContent)(f)

  def apply[A](bp: BodyParser[A])(f: Option[UserEntity] => AuthenticatedRequest[A] => Result)(implicit userRepository: UserRepository) = Authenticated(bp) {
    request =>
      //TODO: fix the await
      Await.result(userRepository.get(request.username), Duration(2, SECONDS)) match {
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

  def async(f: Option[UserEntity] => AuthenticatedRequest[AnyContent] => Future[Result])(implicit userRepository: UserRepository):EssentialAction = async(parse.anyContent)(f)

  def async[A](bp: BodyParser[A])(f: Option[UserEntity] => AuthenticatedRequest[A] => Future[Result])(implicit userRepository: UserRepository) = Authenticated.async(bp) {
    request =>
      userRepository.get(request.username).flatMap {
        case Some(user) =>
          if (user.isAdmin) {
            f(Option(user))(request)
          }
          else {
            Future.successful(Forbidden)
          }
        case none => Future.successful(Forbidden)
      }
  }
}

