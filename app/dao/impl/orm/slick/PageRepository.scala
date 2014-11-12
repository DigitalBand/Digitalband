package dao.impl.orm.slick

import dao.impl.orm.slick.common.RepositoryBase

class PageRepository extends RepositoryBase with dao.common.PageRepository {
  def get(pageName: String): Option[String] = {
    Option("")
  }

  def add(content: String) = {
    ""
  }

  def update(content: String) = {
    ""
  }
}
