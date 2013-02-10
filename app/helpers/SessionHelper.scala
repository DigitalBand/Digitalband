package helpers

import play.api.mvc.Session

object SessionHelper {
  def getCartId(session: Session, createCart: Int => Int, getUserId: String => Int) = session.get("cartid") match {
    case Some(x) => x.toInt
    case None => createCart(session.get("username") match {
      case None => 0
      case Some(x) => getUserId(x)
    })
  }
}
