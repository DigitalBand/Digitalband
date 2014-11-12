package dao.impl.orm.slick

import com.codahale.jerkson.Json
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import models.AboutInfo
import dao.impl.orm.slick.common.RepositoryBase
import play.api._

class AboutRepository extends RepositoryBase with dao.common.AboutRepository {
  val pageKey = "about"

  def get(): Option[AboutInfo] = database withDynSession {
    implicit val res = GetResult(r => AboutInfo(about = r.<<, legalInfo = r.<<))
    sql"""
      select
        a.about_us,
        a.legal_info
      from about a
    """.as[AboutInfo].firstOption
  }

  def save(aboutInfo: AboutInfo) = database withDynSession {
    val jsonInfo = Json.generate(aboutInfo)
    val pageRepository = Global.getControllerInstance(classOf[dao.common.PageRepository])
    val currentAboutInfo = get()

  }
}

