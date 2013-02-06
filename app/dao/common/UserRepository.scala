package dao.common

import models.UserEntity

trait UserRepository {
  def authenticate(login: String, password: String): Option[UserEntity]

}
