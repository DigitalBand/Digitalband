package models

case class PageInfo(id: Int, name: String, alias: String, sections: Seq[PageSection]){
  def this(id: Int, name: String, alias: String) = this(id, name, alias, Seq[PageSection]())
}
