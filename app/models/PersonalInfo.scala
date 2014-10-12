package models

case class PersonalInfo (firstName: String, middleName: String, lastName: String, phone: String, email: Option[String]) {
  def this() = this("", "", "", "",  Option(""))
}
