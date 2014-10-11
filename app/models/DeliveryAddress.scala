package models

case class DeliveryAddress (city: String, street: String, building: String, housing: Option[String], apartment: Option[String]) {
  def this() = this("", "", "", Option(""), Option(""))
}
