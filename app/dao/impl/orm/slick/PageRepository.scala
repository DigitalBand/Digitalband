package dao.impl.orm.slick

import dao.impl.orm.slick.common.RepositoryBase
import slick.driver.JdbcDriver.api._
import models.{PageInfo, PageSection}
import slick.jdbc.GetResult
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PageRepository extends RepositoryBase with dao.common.PageRepository {
  implicit val res = GetResult(r => PageInfo(
    id = r.<<,
    sections = Nil,
    name = r.<<,
    alias = r.<<
  ))

  def get(pageAlias: String): Future[PageInfo] = {
    val pageInfoFuture = usingDB {
      sql"""
      select
        p.id,
        p.name,
        p.alias
      from pages p
      where p.alias = ${pageAlias};
    """.as[PageInfo].head
    }
    for {
      pageInfo <- pageInfoFuture
      sections <- getSections(pageInfo.id)
    } yield PageInfo(pageInfo.id, pageInfo.name, pageInfo.alias, sections)
  }

  def get(pageId: Int): Future[PageInfo] = {
    val pageInfoFuture = usingDB {
      sql"""
      select
        p.id,
        p.name,
        p.alias
      from pages p
      where p.id = ${pageId};
    """.as[PageInfo].head
    }
    for {
      pageInfo <- pageInfoFuture
      sections <- getSections(pageInfo.id)
    } yield PageInfo(pageInfo.id, pageInfo.name, pageInfo.alias, sections)
  }

  def update(page: PageInfo) = {
    val updatedCountFuture = usingDB {
      sql"""
      update pages
      set
        name = ${page.name},
        alias = ${page.alias}
      where id = ${page.id};
    """.as[Int].head
    }
    saveSections(page.id, page.sections)
    updatedCountFuture
  }

  def remove(pageId: Int): Future[Int] = usingDB {
    DBIO.sequence(Seq(
      sql"""
          delete from page_sections where page_id = ${pageId};
        """.as[Int].head,
      sql"""
          delete from pages where id = ${pageId};
        """.as[Int].head)
    ).map(_.sum)
  }

  def list(): Future[Seq[PageInfo]] = usingDB {
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
    """.as[PageInfo]
  }

  def add(page: PageInfo): Future[Int] = {
    val pageIdFuture = usingDB {
      sql"""
      insert into
        pages(name, alias)
        values(${page.name}, ${page.alias});
      select last_insert_id();
    """.as[Int].head
    }
    for {
      pageId <- pageIdFuture
    } yield {
      addSections(pageId, page.sections)
      pageId
    }
  }

  def getSections(pageId: Int): Future[Seq[PageSection]] = usingDB {
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
    """.as[PageSection]
  }

  def addSections(pageId: Int, sections: Seq[PageSection]): Future[Int] = usingDB {
    val updates = sections.map {
      section =>
        sql"""
          insert into
            page_sections(page_id, name, content)
            values(${pageId}, ${section.name}, ${section.content});
        """.as[Int].head
    }
    DBIO.sequence(updates).map(_.sum)
  }

  private def saveSections(pageId: Int, sections: Seq[PageSection]) {
    val sectionsToAdd = sections.filter(s => s.id == 0)
    for {
      currentSections <- getSections(pageId)
    } yield {
      val sectionsToRemove = currentSections.filterNot(section => sections.exists(s => s.id == section.id))
      val sectionsToUpdate = sections.filter(section => currentSections.exists(s => s.id == section.id))
      addSections(pageId, sectionsToAdd)
      updateSections(sectionsToUpdate)
      removeSections(sectionsToRemove)
    }
  }

  def updateSections(sections: Seq[PageSection]): Future[Int] = usingDB {
    val updates = sections.map {
      section =>
        sql"""
          update page_sections
          set
            name = ${section.name},
            content = ${section.content}
          where id = ${section.id};
        """.as[Int].head
    }
    DBIO.sequence(updates).map(_.sum)
  }

  def removeSections(sections: Seq[PageSection]): Future[Int] = usingDB {
    val updates = sections.map {
      section =>
        sql"""
          delete from page_sections where id = ${section.id};
        """.as[Int].head
    }
    DBIO.sequence(updates).map(_.sum)
  }
}
