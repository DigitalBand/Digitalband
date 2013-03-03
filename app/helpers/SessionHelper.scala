package helpers

import play.api.mvc.Session

object SessionHelper {
  def getCartId(createCart: Int => Int, getUserId: String => Int)(implicit session: Session) = session.get("cartid") match {
    case Some(x) => x.toInt
    case None => {
      val userId = session.get("username") match {
        case None => 0
        case Some(x) => getUserId(x)
      }
      createCart(userId)
    }
  }
}
