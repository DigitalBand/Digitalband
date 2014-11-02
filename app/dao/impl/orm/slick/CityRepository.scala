package dao.impl.orm.slick

import dao.impl.orm.slick.common.RepositoryBase
import helpers.PhoneHelper._
import models.{CityShortInfo, ShopInfo, CityInfo}
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import dao.impl.orm.slick.common.RepositoryBase

class CityRepository extends RepositoryBase with dao.common.CityRepository {
  def get(cityId: Int): CityInfo = database withDynSession {
    implicit val result = GetResult(
      r => CityInfo(
        id = r.<<,
        name = r.<<,
        domain = r.<<,
        delivery = r.<<,
        payment = r.<<,
        phone = r.<<,
        prefix = r.<<))
    sql"""
      select
        c.id,
        c.name,
        c.domain,
        c.delivery,
        c.payment,
        c.phone,
        c.prefix
      from cities c
      where
        c.id = ${cityId};
    """.as[CityInfo].first
  }

  def getByHostname(host: String): CityInfo = database withDynSession {
    implicit val result = GetResult(
      r => CityInfo(
        id = r.<<,
        name = r.<<,
        domain = r.<<,
        delivery = r.<<,
        payment = r.<<,
        phone = r.<<,
        prefix = r.<<))
    sql"""
      select
        c.id,
        c.name,
        c.domain,
        c.delivery,
        c.payment,
        c.phone,
        c.prefix
      from cities  c
      where c.domain = ${host};
    """.as[CityInfo].first
  }

  def add(city: CityInfo): Int = database withDynSession {
    sqlu"""
      insert into
        cities(name, domain, delivery, payment, phone, prefix)
        values(${city.name}, ${city.domain}, ${city.delivery}, ${city.payment}, ${city.phone}, ${city.prefix});
    """.execute
    sql"""select last_insert_id();""".as[Int].first
  }

  def update(city: CityInfo) = database withDynSession {
    sqlu"""
      UPDATE cities
      SET
        name = ${city.name},
        domain = ${city.domain},
        delivery = ${city.delivery},
        payment = ${city.payment},
        phone = ${city.phone},
        prefix = ${city.prefix}
      WHERE
        id = ${city.id};
    """.execute
  }

  def remove(cityId: Int) = database withDynSession {
    sqlu"""
      delete from cities where id = ${cityId};
    """.execute
  }

  def listShortInfo: Seq[CityShortInfo] = database withDynSession {
    implicit val res = GetResult(
      r => CityShortInfo(id = r.<<, name = r.<<))
    sql"""
      select
        c.id,
        c.name
      from cities c
    """.as[CityShortInfo].list
  }

  def list: Seq[CityInfo] = database withDynSession {
    implicit val res = GetResult(
      r => CityInfo(
        id = r.<<,
        name = r.<<,
        domain = r.<<,
        delivery = r.<<,
        payment = r.<<,
        phone = r.<<,
        prefix = r.<<))
    sql"""
      select
        c.id,
        c.name,
        c.domain,
        c.delivery,
        c.payment,
        c.phone,
        c.prefix
      from cities c
    """.as[CityInfo].list
  }
}
