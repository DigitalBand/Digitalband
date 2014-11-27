package models

case class PageInfo(id: Int, name: String, alias: String, title: String, sections: Seq[PageSection]){
  def this(id: Int, name: String, alias: String, title: String) = this(id, name, alias, title, Seq[PageSection]())
}
