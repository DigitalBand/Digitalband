package models

case class
OrderDeliveryInfo(address: DeliveryAddress, personalInfo: PersonalInfo, comment: String) {
  def this() = this(new DeliveryAddress(), new PersonalInfo(), "")
}
