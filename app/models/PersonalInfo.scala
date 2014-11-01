package models

case class PersonalInfo (lastName: String,
                         firstName: String,
                         middleName: String,
                         phone: String,
                         email: Option[String],
                         password: Option[String])
{
  def this() = this("", "", "", "",  Option(""), Option(""))

  override def toString(): String = {
    return firstName +
      (if (middleName != "") " " + middleName
      else "") + " " +
      lastName
  }
}
