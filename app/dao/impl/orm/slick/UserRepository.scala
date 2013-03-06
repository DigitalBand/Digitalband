package dao.impl.orm.slick

import models.UserEntity
import common.Profile
import Profile.database
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

class UserRepository extends dao.common.UserRepository {

  def createUser(name: String): Int = database withSession {
    def insertUser(userName: String) = sql"""
         insert into users(sessionId) values('');
        set @userId := (select last_insert_id());
        insert into user_profiles (email, password, userId) values($userName, '', @userId);
        select @userId;
      """.as[Int]
    insertUser(name).first
  }

  def authenticate(login: String, password: String): Option[UserEntity] = database withSession {
    implicit val getUserResult = GetResult(r => new UserEntity(r.<<, r.<<))
    def getUser(name: String, p:String) = sql"""
      select email, userId from user_profiles where email = $name and password = $p;
    """.as[UserEntity]
    getUser(login, password).firstOption
  }

  def get(email: String): Option[UserEntity] = database withSession {
    implicit val getUserResult = GetResult(r => new UserEntity(r.<<, r.<<))
    def getUser(name: String) = sql"""
      select email, userId from user_profiles where email = $name;
    """.as[UserEntity]
    getUser(email).firstOption
  }

  def createUser = database withSession{
    sqlu"""
      insert into users(sessionId) values('');
    """.execute
    sql"""
      select last_insert_id();
    """.as[Int].first
  }

  def remove(userId: Int) = database withSession{
    sqlu"""
      delete from users where userId = $userId;
      delete from user_profiles where userId = $userId;
    """.execute()
  }

  def register(email: String, password: String): Int = database withSession {
    val userId = createUser
    sqlu"""
        insert into user_profiles (email, password, userId) values($email, $password, $userId);
      """.execute
    userId
  }
}
