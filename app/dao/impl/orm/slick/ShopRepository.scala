package dao.impl.orm.slick

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import helpers.PhoneHelper.parsePhones
import models.{CityShortInfo, YandexShopInfo, ShopInfo}
import dao.impl.orm.slick.common.RepositoryBase

class ShopRepository extends RepositoryBase with dao.common.ShopRepository {
  def list: Seq[ShopInfo] = database withDynSession {
    implicit val res = GetResult(r => ShopInfo(
      id = r.<<,
      title = r.<<,
      city = CityShortInfo(r.<<, r.<<),
      address = r.<<,
      phoneNumbers = parsePhones(r.<<)
    ))
    sql"""
      select
        s.id,
        s.title,
        s.city_id,
        c.name,
        s.address,
        s.phones

      from shops s
      inner join cities c on c.id = s.city_id;
    """.as[ShopInfo].list
  }

  override def get(shopId: Int): ShopInfo = database withDynSession {
    implicit val res = GetResult(r => ShopInfo(
      id = r.<<,
      title = r.<<,
      city = CityShortInfo(r.<<, r.<<),
      address = r.<<,
      phoneNumbers = parsePhones(r.<<)))
    sql"""
      select
        s.id,
        s.title,
        s.city_id,
        c.name,
        s.address,
        s.phones
      from shops s
      inner join cities c on c.id = s.city_id
      where
        s.id = ${shopId};
    """.as[ShopInfo].first
  }

  def getByHostname(host: String): Seq[ShopInfo] = database withDynSession{
    implicit val res = GetResult(r => ShopInfo(
      id = r.<<,
      title = r.<<,
      city = CityShortInfo(r.<<, r.<<),
      address = r.<<,
      phoneNumbers = parsePhones(r.<<)
    ))
    sql"""
      select
        s.id,
        s.title,
        s.city_id,
        c.name,
        s.address,
        s.phones
      from shops s
      inner join cities c on c.id = s.city_id
      where
        c.domain = ${host});
    """.as[ShopInfo].list
  }

  def update(shop: ShopInfo) = database withDynSession {
    sqlu"""
      UPDATE shops
      SET
        title = ${shop.title},
        city_id = ${shop.city.id},
        address = ${shop.address},
        phones = ${shop.phoneNumbers.mkString(";")}
      WHERE
        id = ${shop.id};
    """.execute
  }

  def add(shop: ShopInfo): Int = database withDynSession {
    sqlu"""
      insert into
        shops(title, city_id, address, phones)
        values(${shop.title}, ${shop.city.id}, ${shop.address}, ${shop.phoneNumbers.mkString(";")});
    """.execute
    sql"select last_insert_id();".as[Int].first
  }

  def remove(shopId: Int) = database withDynSession {
    sqlu"""
      delete from shops where id = ${shopId};
    """.execute
  }

  //TODO: Check where this data goes
  def getYandexShopInfo = database withDynSession {
    implicit val getYandexShopInfoResults = GetResult(r => YandexShopInfo(r.<<, r.<<, r.<<))
    sql"""
        select
          title,
          company,
          url
        from yandex_shop_info
        where
          shop_id = 1;
    """.as[YandexShopInfo].first
  }
}
