package helpers

import dao.common.UserRepository
import play.api.mvc._
import play.api.mvc.Security.Authenticated
import models.UserEntity
import play.api.Play
import play.api.mvc.BodyParsers._

trait Secured {



  def withAdmin[A](bp: BodyParser[A])(f: Option[UserEntity] => Request[A] => Result)(implicit userRepository: UserRepository) = Authenticated {
    username => Action(bp) {
      implicit request =>
        userRepository.get(username) match {
          case user if user.get.isAdmin => f(user)(request)
          case None => Results.Forbidden
        }
    }
  }
  def withAdmin(f: Option[UserEntity] => Request[AnyContent] => Result)(implicit userRepository: UserRepository): EssentialAction =
    withAdmin(parse.anyContent)(f)
}
