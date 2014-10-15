package dao.impl.orm.slick

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import models.AboutInfo
import dao.impl.orm.slick.common.RepositoryBase

class AboutRepository extends RepositoryBase with dao.common.AboutRepository {
  def get(): AboutInfo = database withDynSession {
    implicit val res = GetResult(r => AboutInfo(about = r.<<, legalInfo = r.<<))
    sql"""
      select
        a.about_us,
        a.legal_info
      from about a
    """.as[AboutInfo].first
  }

  def add(aboutInfo: AboutInfo) = database withDynSession {
    sqlu"""
      insert into
        about(about_us, legal_info)
        values(${aboutInfo.about}, ${aboutInfo.legalInfo});
    """.execute
  }

  def update(aboutInfo: AboutInfo) = database withDynSession {
    sqlu"""
      update about
      set
        about_us = ${aboutInfo.about},
        legal_info = ${aboutInfo.legalInfo}
    """.execute
  }
}

