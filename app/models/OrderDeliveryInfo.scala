package models

case class
OrderDeliveryInfo(address: DeliveryAddress, personalInfo: PersonalInfo, comment: String, register: Boolean) {
  def this() = this(new DeliveryAddress(), new PersonalInfo(), "", false)
}
