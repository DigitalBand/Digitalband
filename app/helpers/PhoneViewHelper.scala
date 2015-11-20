package helpers

import scala.concurrent.Await
import scala.concurrent.duration._

object PhoneViewHelper {
  val cityRepository = db.Global.getControllerInstance(classOf[dao.common.CityRepository])
  def getPhoneNumber(host: String) = {
    Await.result(cityRepository.getByHostname(host), Duration(3, SECONDS)).phone
  }
}
