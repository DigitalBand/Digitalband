package dao.impl.orm.slick

import models.{DeliveryInfo, UserEntity}

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import play.api.Play
import dao.impl.orm.slick.common.RepositoryBase

class UserRepository extends RepositoryBase with dao.common.UserRepository {
  def defaultEmail = Play.current.configuration.getString("email.default").get
  def createUser(name: String): Int = database withDynSession {
    sql"""
         insert into users(sessionId) values('');
        set @userId := (select last_insert_id());
        insert into user_profiles (email, password, user_id) values(${name}, '', @userId);
        select @userId;
      """.as[Int].first
  }


  def authenticate(login: String, password: String): Option[UserEntity] = database withDynSession {
    implicit val getUserResult = GetResult(r => new UserEntity(r.<<, r.<<))
    sql"""
      select
        email, user_id from user_profiles
      where
        email = ${login} and
        password = ${password};
    """.as[UserEntity].firstOption
  }

  def get(email: String): Option[UserEntity] = database withDynSession {
    implicit val getUserResult = GetResult(r => new UserEntity(r.<<, r.<<, r.<<))
    sql"""
      select p.email, p.user_id, ifnull(ur.role_id, 0) role_id
      from
        user_profiles p
      left join users_roles ur on ur.userId = p.user_id
      where
        p.email = ${email};
    """.as[UserEntity].firstOption
  }

  def createUser = database withDynSession {
    sqlu" insert into users(sessionId) values('');".execute
    sql" select last_insert_id();".as[Int].first
  }

  def remove(userId: Int) = database withDynSession {
    sqlu"""
      delete from users where userId = ${userId};
      delete from user_profiles where userId = ${userId};
    """.execute()
  }

  def register(email: String, password: String): Int = database withDynSession {
    val userId = createUser
    sqlu"""
        insert into user_profiles (email, password, user_id) values(${email}, ${password}, ${userId});
      """.execute
    userId
  }

  def updateDeliveryInfo(info: DeliveryInfo, userId: Int) = database withDynSession {
    sqlu"""
      update
        user_profiles
      set
        email = ${info.email},
        user_name = ${info.name},
        phoneNumber = ${info.phone},
        address = ${info.address}
      where
        user_id = ${userId}
    """.execute()
  }

  def getDeliveryInfo(userId: Int) = database withDynSession {
    implicit val deliveryResult = GetResult(r =>
      new DeliveryInfo(
        r.nextStringOption().getOrElse(""),
        r.nextStringOption().getOrElse(""),
        r.nextStringOption().getOrElse(""),
        r.nextStringOption().getOrElse("")))
    sql"""
      select user_name, email, phoneNumber, address from user_profiles where user_id = ${userId};
    """.as[DeliveryInfo].firstOption
  }

  def getAdminEmails: Seq[String] = Play.current.configuration.getString("email.admins") match {
    case Some(config: String) => config.split(";")
    case None => List(defaultEmail)
  }

  def getSystemEmail: String = Play.current.configuration.getString("email.system").getOrElse(defaultEmail)

  def getPassword(email: String) = database withDynSession {
    sql"""
      select p.password
      from
        user_profiles p
      where
        p.email = ${email};
    """.as[String].firstOption
  }
}
