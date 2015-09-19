package helpers

object PhoneViewHelper {
  val cityRepository = db.Global.getControllerInstance(classOf[dao.common.CityRepository])
  def getPhoneNumber(host: String) = {
    val shopInfos = cityRepository.getByHostname(host)
  }
}
