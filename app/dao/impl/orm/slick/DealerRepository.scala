package dao.impl.orm.slick

import dao.impl.orm.slick.common.Profile
import Profile.database._
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

import slick.jdbc.{StaticQuery => Q, GetResult}

import models.DealerInfo

/**
 * Created by tim on 17/02/14.
 */
class DealerRepository extends dao.common.DealerRepository {
  override def list: Seq[DealerInfo] = withSession {
    implicit val getDealers = GetResult(r => DealerInfo(r.<<, r.<<))
    sql"""
      select id, title from dealers;
    """.as[DealerInfo].list
  }
}
