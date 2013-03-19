package helpers

import play.api.mvc.Session
import play.api.Play

object SessionHelper {
  lazy val username: String = Play.maybeApplication.flatMap(_.configuration.getString("session.username")) getOrElse ("username")
  def getUserId(createUser: => Int, getUserId: (String) => Int)(implicit session: Session): Int = {
     session.get("userid") match {
       case Some(x) => x.toInt
       case None => {
         session.get("email") match {
           case None => createUser
           case Some(userName) => getUserId(userName)
         }
       }
     }
  }

}
