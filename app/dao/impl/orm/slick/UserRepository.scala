package dao.impl.orm.slick

import models.UserEntity
import common.Profile
import Profile.database
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}

class UserRepository extends dao.common.UserRepository {

  //TODO: Finish this function (omit concat parameters)
  def createUser(name: String): Int = {
    database withSession {
      val query = Q.queryNA[Int](s"""
        insert into users(sessionId) values('');
        set @userId := (select last_insert_id());
        insert into user_profiles (email, password, userId) values($name, '', @userId);
        select @userId;
       """)


      query.first()
      ???
    }
  }

  def authenticate(login: String, password: String): Int = ???

  def get(email: String): Option[UserEntity] = ???

  def getUserId(name: String) = {
    get(name) match {
      case Some(user) => user.id
      case None => 0
    }
  }
}
