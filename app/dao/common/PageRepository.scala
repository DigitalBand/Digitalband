package dao.common

trait PageRepository {
  def get(pageName: String): Option[String]
  def add(content: String)
  def update(content: String)
}
