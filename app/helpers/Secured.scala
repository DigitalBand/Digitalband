package helpers

import dao.common.UserRepository
import play.api.mvc._
import play.api.mvc.Security.Authenticated
import models.UserEntity
import play.api.Play

trait Secured {
  def withAdmin(f: Option[UserEntity] => Request[AnyContent] => Result)(implicit userRepository: UserRepository) = Authenticated {
    username => Action {
      implicit request =>
        userRepository.get(username) match {
          case user if user.get.isAdmin => f(user)(request)
          case None => Results.Forbidden
        }
    }
  }
}
