package dao.impl.orm.slick

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import dao.impl.orm.slick.common.RepositoryBase
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import models.{PageInfo, PageSection}

import scala.slick.jdbc.GetResult

class PageRepository extends RepositoryBase with dao.common.PageRepository {
  def get(pageAlias: String): PageInfo = database withDynSession {
    implicit val res = GetResult(r => PageInfo(
      id = r.<<,
      sections = getSections(r.<<),
      name = r.<<,
      alias = r.<<
    ))
    sql"""
      select
        p.id,
        p.id,
        p.name,
        p.alias,
        p.title
      from pages p
      where p.alias = ${pageAlias};
    """.as[PageInfo].first
  }

  def get(pageId: Int): PageInfo = database withDynSession {
    implicit val res = GetResult(r => PageInfo(
      id = r.<<,
      name = r.<<,
      alias = r.<<,
      sections = getSections(pageId)
    ))
    sql"""
      select
        p.id,
        p.name,
        p.alias,
        p.title
      from pages p
      where p.id = ${pageId};
    """.as[PageInfo].first
  }

  def update(page: PageInfo) = database withDynSession {
    sqlu"""
      update pages
      set
        name = ${page.name},
        alias = ${page.alias}
      where id = ${page.id};
    """.execute

    saveSections(page.id, page.sections)
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
      name = r.<<,
      alias = r.<<,
      sections = Seq[PageSection]()
    ))
    sql"""
      select
        p.id,
        p.name,
        p.alias,
        p.title
      from pages p;
    """.as[PageInfo].list
  }

  def add(page: PageInfo): Int = database withDynSession {
    sqlu"""
      insert into
        pages(name, alias)
        values(${page.name}, ${page.alias});
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
    sections.foreach {
      section =>
        sqlu"""
          insert into
            page_sections(page_id, name, content)
            values(${pageId}, ${section.name}, ${section.content});
        """.execute
    }
  }

  def saveSections(pageId: Int, sections: Seq[PageSection]) {
    val currentSections = getSections(pageId)
    val sectionsToAdd = sections.filter(s => s.id == 0)
    val sectionsToRemove = currentSections.filterNot(section => sections.exists(s => s.id == section.id))
    val sectionsToUpdate = sections.filter(section => currentSections.exists(s => s.id == section.id))

    addSections(pageId, sectionsToAdd)
    updateSections(sectionsToUpdate)
    removeSections(sectionsToRemove)
  }

  def updateSections(sections: Seq[PageSection]) {
    sections.foreach {
      section =>
        sqlu"""
          update page_sections
          set
            name = ${section.name},
            content = ${section.content}
          where id = ${section.id};
        """.execute
    }
  }

  def removeSections(sections: Seq[PageSection]) {
    sections.foreach {
      section =>
        sqlu"""
          delete from page_sections where id = ${section.id};
        """.execute
    }
  }
}
