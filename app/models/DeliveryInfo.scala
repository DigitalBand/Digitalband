package models

case class DeliveryInfo(name: String, email: String, phone: String, address: String){
  def this() = this("", "", "", "")
}
