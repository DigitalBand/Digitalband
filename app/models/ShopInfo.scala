package models

import play.api.libs.json.Json

case class ShopInfo(id: Int, title: String, cityId: Option[Int], cityName: String, address: String, phoneNumbers: Seq[String])

object ShopInfo {
  implicit val shopInfoFormat = Json.format[ShopInfo]
}
