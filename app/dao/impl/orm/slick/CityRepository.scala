package dao.impl.orm.slick

import dao.impl.orm.slick.common.RepositoryBase
import helpers.PhoneHelper._
import models.{ShopInfo, CityInfo}
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import dao.impl.orm.slick.common.RepositoryBase

class CityRepository extends RepositoryBase with dao.common.CityRepository {
  def get(cityId: Int): CityInfo = database withDynSession {
    implicit val result = GetResult(
      r => CityInfo(id = r.<<, name = r.<<, domain = r.<<, delivery = r.<<, payment = r.<<))
    sql"""
      select
        c.id,
        c.name,
        c.domain,
        c.delivery,
        c.payment
      from cities c
      where
        c.id = ${cityId};
    """.as[CityInfo].first
  }

  def getByHostname(host: String): CityInfo = database withDynSession{
    implicit val result = GetResult(
      r => CityInfo(id = r.<<, name = r.<<, domain = r.<<, delivery = r.<<, payment = r.<<))
    sql"""
      select
        c.id,
        c.name,
        c.domain,
        c.delivery,
        c.payment
      from cities  c
      where c.domain = ${host};
    """.as[CityInfo].first
  }

  def add(city: CityInfo): Int = database withDynSession {
    sqlu"""

           """
    sql"""select last_insert_id();""".as[Int].first
  }

  def update(city: CityInfo) = {

  }

  def remove(cityId: Int) = database withDynSession {
    sqlu"""
      delete from cities where id = ${cityId};
    """.execute
  }

  def list: Seq[CityInfo] = database withDynSession {
    implicit val res = GetResult(
      r => CityInfo(id = r.<<, name = r.<<, domain = r.<<, delivery = r.<<, payment = r.<<))
    sql"""
      select
        c.id,
        c.name,
        c.domain,
        c.delivery,
        c.payment
      from cities c
    """.as[CityInfo].list
  }
}
