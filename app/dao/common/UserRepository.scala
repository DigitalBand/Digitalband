package dao.common

import models.{DeliveryInfo, UserEntity}

trait UserRepository {
  //TODO: implement this. Cache it
  def getAdminEmails: Seq[String] = List("tim@digitalband.ru")

  //TODO: move this hardcoded string to DB. Cache it
  def getSystemEmail = "tim@digitalband.ru"

  def getDeliveryInfo(userId: Int): Option[DeliveryInfo]

  def updateDeliveryInfo(info: DeliveryInfo, userId: Int)

  def remove(userId: Int)

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
