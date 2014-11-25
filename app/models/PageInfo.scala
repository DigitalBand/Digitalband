package models

case class PageInfo(id: Int, name: String, alias: String, title:String, sections: Seq[PageSection])
