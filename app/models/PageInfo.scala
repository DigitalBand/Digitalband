package models

import play.api.libs.json._

case class PageInfo(id: Int, name: String, alias: String, sections: Seq[PageSection]){
  def this(id: Int, name: String, alias: String) = this(id, name, alias, Seq[PageSection]())
}

object PageInfo {
  implicit val pageInfoReads = Json.reads[PageInfo]
  implicit val pageInfoWrites = Json.writes[PageInfo]
}