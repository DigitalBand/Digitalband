package dao.impl.fake

import models.UserEntity

class UserRepository extends dao.common.UserRepository {
  def authenticate(login: String, password: String): Option[UserEntity] = {
    if (login.equals("none")) None else Option(UserEntity("user@domain.ru"))
  }
}
