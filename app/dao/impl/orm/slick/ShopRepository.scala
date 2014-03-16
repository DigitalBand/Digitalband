package dao.impl.orm.slick

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import helpers.PhoneHelper.parsePhones
import models.ShopInfo
import dao.impl.orm.slick.common.RepositoryBase

class ShopRepository extends RepositoryBase with dao.common.ShopRepository {
  def list: Seq[ShopInfo] = database withDynSession {
    implicit val res = GetResult(r => ShopInfo(r.<<, r.<<, r.<<, r.<<, parsePhones(r.<<)))
    sql"""
      select
        s.id,
        s.title,
        s.city,
        s.address,
        s.phones
      from shops s
    """.as[ShopInfo].list
  }

  override def get(shopId: Int): ShopInfo = database withDynSession {
    implicit val res = GetResult(r => ShopInfo(r.<<, r.<<, r.<<, r.<<, parsePhones(r.<<)))
    sql"""
      select
        s.id,
        s.title,
        s.city,
        s.address,
        s.phones
      from shops s
      where
        s.id = ${shopId};
    """.as[ShopInfo].first
  }



  def update(shop: ShopInfo) = database withDynSession {
    sqlu"""
      UPDATE shops
      SET
        title = ${shop.title},
        city = ${shop.city},
        address = ${shop.address},
        phones = ${shop.phoneNumbers.mkString(";")}
      WHERE
        id = ${shop.id};
    """.execute
  }

  def add(shop: ShopInfo): Int = database withDynSession {
    sqlu"""
      insert into
        shops(title, city, address, phones)
        values(${shop.title}, ${shop.city}, ${shop.address}, ${shop.phoneNumbers.mkString(";")});
    """.execute
    sql"""select last_insert_id();""".as[Int].first
  }

  def remove(shopId: Int) = database withDynSession {
    sqlu"""
      delete from shops where id = ${shopId};
    """.execute
  }
}
