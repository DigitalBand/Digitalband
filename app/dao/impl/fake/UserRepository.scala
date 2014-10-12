package dao.impl.fake

import models.{PersonalInfo, DeliveryAddress, DeliveryInfo, UserEntity}

class UserRepository extends dao.common.UserRepository {
  def authenticate(login: String, password: String): Option[UserEntity] = {
    if (login.equals("none@none.ru")) None else Option(new UserEntity("user@domain.ru", 1))
  }

  def get(email: String): Option[UserEntity] = if (email.equals("none@none.ru")) None else Option(new UserEntity("user@domain.ru", 1))

  def createUser(name: String) = ???

  def createUser = ???

  def remove(userId: Int) = ???

  def register(email: String, password: String): Int = ???

  def updateDeliveryInfo(info: DeliveryInfo, userId: Int) = ???

  def updateDeliveryAddress(address: DeliveryAddress, userId: Int) = ???

  def getDeliveryInfo(userId: Int) = ???

  def getAdminEmails = ???

  def getSystemEmail = ???

  def getPassword(email: String) = ???

  def getDeliveryAddress(userId: Int): Option[DeliveryAddress] = ???

  def getPersonalInfo(userId: Int): Option[PersonalInfo] = ???

  def updatePersonalInfo(info: PersonalInfo, userId: Int): Unit = ???
}
