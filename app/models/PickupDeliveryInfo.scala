package models

case class PickupDeliveryInfo(shopId: Int, personalInfo: PersonalInfo, comment: String) {
  def this() = this(0, new PersonalInfo(), "")
}
