package dao.common

import models.UserEntity

trait UserRepository {
  def createUser(name: String): Int

  def createUser: Int

  def get(email: String): Option[UserEntity]

  def authenticate(login: String, password: String): Option[UserEntity]

  def getUserId(name: String) = {
    get(name) match {
      case Some(user) => user.id
      case None => 0
    }
  }
}
