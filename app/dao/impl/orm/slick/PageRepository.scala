package dao.impl.orm.slick

import dao.impl.orm.slick.common.RepositoryBase
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import play.utils.UriEncoding

class PageRepository extends RepositoryBase with dao.common.PageRepository {
  def get(pageName: String): Option[String] = database withDynSession {
    implicit val res = GetResult(r => r)
    sql"""
      select
        p.content
      from pages p
      where p.name = ${pageName}
    """.as[String].firstOption
  }

  def add(pageName: String, content: String) = database withDynSession {
    sqlu"""
     insert into
      pages(name, content)
     values(${pageName}, ${content});
   """.execute
  }

  def update(pageName: String, content: String) = database withDynSession {
    sqlu"""
      update pages
      set
        content = ${content},
      where name = ${pageName}
    """.execute
  }
}
