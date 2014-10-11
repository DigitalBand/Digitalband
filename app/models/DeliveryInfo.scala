package models

case class DeliveryInfo(name: String, middleName: String, lastName: String, email: Option[String], phone: String, address: String){
  def this() = this("", "", "", Option(""), "", "")
}
