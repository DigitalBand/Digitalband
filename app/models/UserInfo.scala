package models

case class UserInfo(id: Int, personalInfo: PersonalInfo, address: Option[DeliveryAddress]) {
  def this() = this(0, new PersonalInfo(), Option(new DeliveryAddress()))
}
