package dao.impl.orm.slick

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import models.DealerInfo
import dao.impl.orm.slick.common.RepositoryBase


class DealerRepository extends RepositoryBase with dao.common.DealerRepository {

  override def list: Seq[DealerInfo] = database withDynSession {
    implicit val getDealers = GetResult(r => DealerInfo(r.<<, r.<<))
    sql"""
      select id, title from dealers;
    """.as[DealerInfo].list
  }
}
