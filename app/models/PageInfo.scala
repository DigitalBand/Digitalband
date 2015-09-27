package models

import play.api.libs.json._

case class PageInfo(id: Int, name: String, alias: String, sections: Seq[PageSection])

object PageInfo {
  implicit val jsonFormat = Json.format[PageInfo]
}
