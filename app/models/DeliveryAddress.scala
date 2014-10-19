package models

import play.api.i18n.Messages

case class DeliveryAddress (city: String, street: String, building: String, housing: Option[String], apartment: Option[String]) {
  def this() = this("", "", "", Option(""), Option(""))

  override def toString(): String = {
    return Messages("address.city") + ": " +  city + ", " +
      Messages("address.street") + ": " + street + ", " +
      Messages("address.building") + ": " + building + ", " +
      Messages("address.housing") + ": " + housing.getOrElse("") + ", " +
      Messages("address.apartment") + ": " + apartment.getOrElse("")
  }
}
