package models

import play.api.libs.json._

case class PageSection(id: Int, name: String, content: String)

object PageSection {
  implicit val pageSectionReads = Json.reads[PageSection]
  implicit val pageSectionWrites = Json.writes[PageSection]
}