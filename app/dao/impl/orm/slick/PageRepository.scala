package dao.impl.orm.slick

import dao.impl.orm.slick.common.RepositoryBase
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import play.utils.UriEncoding

class PageRepository extends RepositoryBase with dao.common.PageRepository {
  val charset = "utf-8"

  def get(pageName: String): Option[String] = database withDynSession {
    implicit val res = GetResult(r => r)
    val content = sql"""
      select
        p.content
      from pages p
      where p.name = ${pageName}
    """.as[String].firstOption
    if (content.isDefined)
      Option(UriEncoding.decodePath(content.get, charset))
    else
      content
  }

  def add(pageName: String, content: String) = database withDynSession {
    val encodedContent = UriEncoding.encodePathSegment(content, charset)
    sqlu"""
     insert into
      pages(name, content)
     values(${pageName}, ${encodedContent});
   """.execute
  }

  def update(pageName: String, content: String) = database withDynSession {
    val encodedContent = UriEncoding.encodePathSegment(content, charset)
    sqlu"""
      update pages
      set
        content = ${encodedContent}
      where name = ${pageName}
    """.execute
  }
}
