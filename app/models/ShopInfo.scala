package models

case class ShopInfo(id: Int, title: String, cityId: Option[Int], cityName: String, address: String, phoneNumbers: Seq[String])
