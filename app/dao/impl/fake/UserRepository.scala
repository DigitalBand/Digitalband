package dao.impl.fake

import models.UserEntity

class UserRepository extends dao.common.UserRepository {
  def authenticate(login: String, password: String): Option[UserEntity] = {
    if (login.equals("none@none.ru")) None else Option(UserEntity("user@domain.ru"))
  }

  def get(email: String): Option[UserEntity] = if (email.equals("none@none.ru")) None else Option(UserEntity("user@domain.ru"))
}
