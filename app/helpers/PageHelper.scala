package helpers

import scala.concurrent.Await
import scala.concurrent.duration._

object PageHelper {
  val pageRepository = db.Global.getControllerInstance(classOf[dao.common.PageRepository])
  def getPages() = Await.result(pageRepository.list, Duration(3, SECONDS))
}
