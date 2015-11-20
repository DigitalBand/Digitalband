package dao.common

import models._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait UserRepository {

  def getAdminEmails: Seq[String]

  def getSystemEmail: String

  def getDeliveryInfo(userId: Int): Future[Option[DeliveryInfo]]

  def getUserInfo(userId: Int): Future[Option[UserInfo]]

  def updateDeliveryInfo(info: DeliveryInfo, userId: Int): Future[Int]

  def updateUserInfo(info: UserInfo): Future[Int]

  def remove(userId: Int): Future[Int]

  def getPassword(email: String): Future[Option[String]]

  def register(email:String, password:String): Future[Int]

  def createUser(name: String): Future[Int]

  def createUser: Future[Int]

  def get(email: String): Future[Option[UserEntity]]

  def authenticate(login: String, password: String): Future[Option[UserEntity]]

  def getUserId(name: String): Future[Int] = {
    for {
      user <- get(name)
    } yield {
      user match {
        case Some(u) => u.id
        case None => 0
      }
    }
  }
}
