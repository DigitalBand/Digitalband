package dao.impl.orm.slick

import models._
import slick.driver.MySQLDriver.api._
import slick.jdbc.GetResult
import play.api.Play
import dao.impl.orm.slick.common.RepositoryBase
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserRepository extends RepositoryBase with dao.common.UserRepository {
  def defaultEmail = Play.current.configuration.getString("email.default").get

  def createUser(name: String): Future[Int] = usingDB {
    sql"""
      insert into users(session_id) values('');
      set @userId := (select last_insert_id());
      insert into user_profiles (email, password, user_id) values(${name}, '', @userId);
      select @userId;
    """.as[Int].head
  }

  def authenticate(login: String, password: String): Future[Option[UserEntity]] = usingDB {
    implicit val getUserResult = GetResult(r => new UserEntity(r.<<, r.<<))
    sql"""
      select
        email, user_id from user_profiles
      where
        email = ${login} and
        password = ${password};
    """.as[UserEntity].headOption
  }

  def get(email: String): Future[Option[UserEntity]] = usingDB {
    implicit val getUserResult = GetResult(r => new UserEntity(r.<<, r.<<, r.<<))
    sql"""
      select p.email, p.user_id, ifnull(ur.role_id, 0) role_id
      from
        user_profiles p
      left join users_roles ur on ur.user_id = p.user_id
      where
        p.email = ${email};
    """.as[UserEntity].headOption
  }

  def createUser: Future[Int] = usingDB {
    returningId(sql"""
      insert into users(session_id) values('');
    """.as[Int].head)
  }

  def remove(userId: Int): Future[Int] = usingDB {
    sql"""
      delete from users where id = ${userId};
      delete from user_profiles where user_id = ${userId};
    """.as[Int].head
  }

  def register(email: String, password: String): Future[Int] = {
    def updateUserProfileFuture(userId: Int) = usingDB {
      sql"""
      insert into user_profiles (email, password, user_id) values(${email}, ${password}, ${userId});
    """.as[Int].head
    }
    for {
      userId <- createUser
      updatedCount <- updateUserProfileFuture(userId)
    } yield userId
  }

  def updateDeliveryInfo(info: DeliveryInfo, userId: Int): Future[Int] = usingDB {
    sql"""
      update
        user_profiles
      set
        email = ${info.email},
        user_name = ${info.name},
        phone_number = ${info.phone},
        address = ${info.address}
      where
        user_id = ${userId}
    """.as[Int].head
  }

  def updateUserInfo(info: UserInfo): Future[Int] = {
    val updateUserProfiles = usingDB {
      sql"""
        update
          user_profiles
        set
          user_name = ${info.personalInfo.firstName},
          user_last_name = ${info.personalInfo.lastName},
          user_middle_name = ${info.personalInfo.middleName},
          email = ${info.personalInfo.email},
          phone_number = ${info.personalInfo.phone}
        where
          user_id = ${info.id}
      """.as[Int].head
    }

    if (info.address.isDefined)
    {
      val address = info.address.get
      usingDB {
        sql"""
          update
            user_profiles
          set
            city = ${address.city},
            street = ${address.street},
            building = ${address.building},
            apartment = ${address.apartment}
          where
            user_id = ${info.id}
        """.as[Int].head
      }
    }
    updateUserProfiles
  }

  def getDeliveryInfo(userId: Int): Future[Option[DeliveryInfo]] = usingDB {
    implicit val deliveryResult = GetResult(r =>
      new DeliveryInfo(
        r.nextStringOption().getOrElse(""),
        r.nextStringOption().getOrElse(""),
        r.nextStringOption().getOrElse(""),
        r.nextStringOption().getOrElse("")))
    sql"""
      select user_name, email, phone_number, address from user_profiles where user_id = ${userId};
    """.as[DeliveryInfo].headOption
  }

  def getUserInfo(userId: Int) = usingDB {
    implicit val result = GetResult(r =>
      new UserInfo(
        id = userId ,
        personalInfo = new PersonalInfo(firstName = r.<<, lastName = r.<<, middleName = r.<<, email = r.<<, phone = r.<<),
        address = Option(new DeliveryAddress(city = r.<<, street = r.<<, building = r.<<, apartment = r.<<))))
    sql"""
      select
        user_name,
        user_last_name,
        user_middle_name,
        email,
        phone_number,
        city,
        street,
        building,
        apartment
      from user_profiles
      where user_id = ${userId};
    """.as[UserInfo].headOption
  }

  def getAdminEmails: Seq[String] = Play.current.configuration.getString("email.admins") match {
    case Some(config: String) => config.split(";")
    case None => List(defaultEmail)
  }

  def getSystemEmail: String = Play.current.configuration.getString("email.system").getOrElse(defaultEmail)

  def getPassword(email: String): Future[Option[String]] = usingDB {
    sql"""
      select p.password
      from
        user_profiles p
      where
        p.email = ${email};
    """.as[String].headOption
  }
}
