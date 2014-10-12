package dao.common

import models.{PersonalInfo, DeliveryAddress, DeliveryInfo, UserEntity}


trait UserRepository {

  def getAdminEmails: Seq[String]

  def getSystemEmail: String

  def getDeliveryInfo(userId: Int): Option[DeliveryInfo]

  def getDeliveryAddress(userId: Int): Option[DeliveryAddress]

  def getPersonalInfo(userId: Int): Option[PersonalInfo]

  def updateDeliveryInfo(info: DeliveryInfo, userId: Int)

  def updateDeliveryAddress(address: DeliveryAddress, userId: Int)

  def updatePersonalInfo(info: PersonalInfo, userId: Int)

  def remove(userId: Int)

  def getPassword(email: String): Option[String]

  def register(email:String, password:String): Int

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
