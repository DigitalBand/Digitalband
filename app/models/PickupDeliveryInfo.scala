package models

case class PickupDeliveryInfo(shopId: Int, personalInfo: PersonalInfo, comment: String, register: Boolean) {
  def this() = this(0, new PersonalInfo(), "", false)
}
