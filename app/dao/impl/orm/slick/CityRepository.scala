package dao.impl.orm.slick

import models.{CityShortInfo, CityInfo}
import slick.driver.MySQLDriver.backend.Database
import slick.driver.MySQLDriver.api._
import slick.jdbc.{StaticQuery => Q, GetResult}

import dao.impl.orm.slick.common.RepositoryBase

import scala.concurrent.Future


class CityRepository extends RepositoryBase with dao.common.CityRepository {
  def get(cityId: Int): Future[CityInfo] = getDB.run {
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
    """.as[CityInfo].head
  }

  def getByHostname(host: String) = getDB.run {
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
    """.as[CityInfo].head
  }


  def add(city: CityInfo): Future[Int] = getDB.run {
    sql"""
      insert into
        cities(name, domain, delivery, payment, phone, prefix)
        values(${city.name}, ${city.domain}, ${city.delivery}, ${city.payment}, ${city.phone}, ${city.prefix});
        select last_insert_id();
    """.as[Int].head
  }

  def update(city: CityInfo) = getDB.run {
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
    """
  }

  def remove(cityId: Int) = getDB.run {
    sqlu"""
      delete from cities where id = ${cityId};
    """
  }

  def listShortInfo: Future[Seq[CityShortInfo]] = getDB.run {
    implicit val res = GetResult(
      r => CityShortInfo(id = r.<<, name = r.<<))
    sql"""
      select
        c.id,
        c.name
      from cities c
    """.as[CityShortInfo]
  }

  def list: Future[Seq[CityInfo]] = getDB.run {
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
    """.as[CityInfo]
  }
}
