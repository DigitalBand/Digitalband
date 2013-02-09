package dao.impl.fake

import models.UserEntity

class UserRepository extends dao.common.UserRepository {
  def authenticate(login: String, password: String): Option[UserEntity] = {
    if (login.equals("none@none.ru")) None else Option(new UserEntity("user@domain.ru", 1))
  }

  def get(email: String): Option[UserEntity] = if (email.equals("none@none.ru")) None else Option(new UserEntity("user@domain.ru", 1))
}
