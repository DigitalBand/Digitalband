package dao.impl.orm.slick

import dao.impl.orm.slick.common.Profile
import Profile.database._
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

import models.ShopInfo

class ShopRepository extends dao.common.ShopRepository {
  def list: Seq[ShopInfo] = withSession {
    implicit val res = GetResult(r => ShopInfo(r.<<, r.<<, r.<<, r.<<, parsePhones(r.<<)))
    sql"""
      select
        s.id,
        s.title,
        s.city,
        s.address,
        s.phones
      from shop s
    """.as[ShopInfo].list
  }

  def parsePhones(phones: Option[String]): Seq[String] = phones match {
    case Some(phones) => phones.split(";")
    case None => Nil
  }

  def update(shop: ShopInfo) = withSession {
    sqlu"""
      UPDATE shop
      SET
        title = ${shop.title},
        city = ${shop.city},
        address = ${shop.address},
        phones = ${shop.phoneNumbers.mkString(";")}
      WHERE
        id = ${shop.id};
    """.execute
  }

  def add(shop: ShopInfo): Int = withSession {
    sqlu"""
      insert into
        shop(title, city, address, phones)
        values(${shop.title}, ${shop.city}, ${shop.address}, ${shop.phoneNumbers.mkString(";")});
    """.execute
    sql"""select last_insert_id();""".as[Int].first
  }

  def remove(shopId: Int) = withSession {
    sqlu"""
      delete from shop where id = ${shopId};
    """.execute
  }
}
