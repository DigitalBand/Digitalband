package dao.impl.orm.slick

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import dao.impl.orm.slick.common.RepositoryBase
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import models.{PageInfo, PageSection}

import scala.slick.jdbc.GetResult

class PageRepository extends RepositoryBase with dao.common.PageRepository {
  def get(pageId: Int): PageInfo = database withDynSession {
    implicit val res = GetResult(r => PageInfo(
      id = r.<<,
      name = r.<<,
      sections = getSections(pageId)
    ))
    sql"""
      select
        p.id,
        p.name
      from pages p
      where p.id = ${pageId};
    """.as[PageInfo].first
  }

  def update(page: PageInfo) = database withDynSession {

  }

  def remove(pageId: Int) = database withDynSession {
    sqlu"""
      delete from page_sections where page_id = ${pageId};
    """.execute

    sqlu"""
      delete from pages where id = ${pageId};
    """.execute
  }

  def list(): Seq[PageInfo] = database withDynSession {
    implicit val res = GetResult(r => PageInfo(
      id = r.<<,
      sections = getSections(2),
      name = r.<<
    ))
    sql"""
      select
        p.id,
        p.name
      from pages p;
    """.as[PageInfo].list
  }

  def add(page: PageInfo): Int = database withDynSession {
    sqlu"""
      insert into
        pages(name)
        values(${page.name});
    """.execute
    val pageId = sql"select last_insert_id();".as[Int].first
    addSections(pageId, page.sections)
    pageId
  }

  def getSections(pageId: Int): Seq[PageSection] = {
    implicit val res = GetResult(r => PageSection(
      id = r.<<,
      name = r.<<,
      content = r.<<
    ))
    sql"""
      select
        ps.id,
        ps.name,
        ps.content
      from page_sections ps
      where ps.page_id = ${pageId};
    """.as[PageSection].list
  }

  def addSections(pageId: Int, sections: Seq[PageSection]) {
    var sb = new StringBuilder
    for(section <- sections){
      sqlu"""
      insert into
        page_sections(page_id, name, content)
        values(${pageId}, ${section.name}, ${section.content});
    """.execute
    }
  }
}
