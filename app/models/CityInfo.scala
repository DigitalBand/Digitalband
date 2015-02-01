package models
import play.api.libs.json._
case class CityShortInfo(id: Int, name: String)
case class CityInfo (id: Int,
                     name: String,
                     domain: String,
                     delivery: String,
                     payment: String,
                     phone: String,
                     prefix: Option[String])

object CityInfo {
  implicit val reads = Json.reads[CityInfo]
  implicit val writes = Json.writes[CityInfo]
}
object CityShortInfo {
  implicit val reads = Json.reads[CityShortInfo]
  implicit val writes = Json.writes[CityShortInfo]
}
