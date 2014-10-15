package models

case class CityShortInfo(id: Int, name: String)
case class ShopInfo(id: Int, title: String, city: CityShortInfo, address: String, phoneNumbers: Seq[String])
