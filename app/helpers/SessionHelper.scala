package helpers

import play.api.mvc.Session
import play.api.Play

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object SessionHelper {
  lazy val username: String = Play.maybeApplication.flatMap(_.configuration.getString("session.username")) getOrElse ("username")
  //TODO: remove await
  def getUserId(createUser: => Future[Int], getUserId: String => Future[Int])(implicit session: Session): Int = {
     session.get("userid") match {
       case Some(x) => x.toInt
       case None => {
         session.get("email") match {
           case None => Await.result(createUser, Duration(2, SECONDS))
           case Some(userName) => Await.result(getUserId(userName), Duration(2, SECONDS))
         }
       }
     }
  }

}
