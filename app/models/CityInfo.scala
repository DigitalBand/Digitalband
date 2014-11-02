package models

case class CityShortInfo(id: Int, name: String)
case class CityInfo (id: Int,
                     name: String,
                     domain: String,
                     delivery: String,
                     payment: String,
                     phone: String,
                     prefix: Option[String])