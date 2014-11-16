package models

case class PageInfo(id: Int, name: String, sectionsCount: Int, sections: Seq[PageSection])
