package helpers

import play.api.mvc.Session

object SessionHelper {
  def getUserId(createUser: => Int, getUserId: (String) => Int)(implicit session: Session): Int = {
     session.get("userid") match {
       case Some(x) => x.toInt
       case None => {
         session.get("username") match {
           case None => createUser
           case Some(userName) => getUserId(userName)
         }
       }
     }
  }

}
