package dao.impl.orm.slick

import slick.driver.MySQLDriver.api._
import slick.jdbc.{GetResult}
import models.DealerInfo
import dao.impl.orm.slick.common.RepositoryBase

import scala.concurrent.Future


class DealerRepository extends RepositoryBase with dao.common.DealerRepository {
  override def list: Future[Seq[DealerInfo]] = usingDB {
    implicit val getDealers = GetResult(r => DealerInfo(r.<<, r.<<))
    sql"""
      SELECT id, title FROM dealers;
    """.as[DealerInfo]
  }
}
