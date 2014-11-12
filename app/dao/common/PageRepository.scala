package dao.common

trait PageRepository {
  def get(pageName: String): Option[String]
  def add(pageName: String, content: String)
  def update(pageName: String, content: String)
}
