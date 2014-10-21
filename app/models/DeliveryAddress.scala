package models

import play.api.i18n.Messages

case class DeliveryAddress (city: String, street: String, building: String, apartment: Option[String]) {
  def this() = this("", "", "", Option(""))

  override def toString(): String = {
    return city + ", " + street + " " + building +
      (if (apartment.isDefined) ", " + Messages("address.shortApartment") + " " + apartment.get
        else "")
  }
}
