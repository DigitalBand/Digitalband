package dao.common

import models.UserEntity

trait UserRepository {
  def get(email: String): Option[UserEntity]

  def authenticate(login: String, password: String): Option[UserEntity]

  def getUserId(name: String): Int
}
