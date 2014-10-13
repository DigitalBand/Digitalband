package models

case class PersonalInfo (lastName: String, firstName: String, middleName: String, phone: String, email: Option[String]) {
  def this() = this("", "", "", "",  Option(""))
}
