package dao.impl.orm.slick

import slick.driver.MySQLDriver.api._
import dao.impl.orm.slick.common.RepositoryBase
import helpers.PhoneHelper.parsePhones
import models.{ShopInfo, YandexShopInfo}
import slick.jdbc.GetResult

import scala.concurrent.Future


class ShopRepository extends RepositoryBase with dao.common.ShopRepository {
  def list: Future[Seq[ShopInfo]] = usingDB {
    implicit val res = GetResult(r => ShopInfo(
      id = r.<<,
      title = r.<<,
      cityId = r.<<,
      cityName = r.<<,
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
    """.as[ShopInfo]
  }

  override def get(shopId: Int): Future[ShopInfo] = usingDB {
    implicit val res = GetResult(r => ShopInfo(
      id = r.<<,
      title = r.<<,
      cityId = r.<<,
      cityName = r.<<,
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
        s.id = ${shopId};
    """.as[ShopInfo].head
  }

  def getByCity(cityId: Int): Future[Seq[ShopInfo]] = usingDB {
    implicit val res = GetResult(r => ShopInfo(
      id = r.<<,
      title = r.<<,
      cityId = r.<<,
      cityName = r.<<,
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
        s.city_id = ${cityId};
    """.as[ShopInfo]
  }

  def getByHostname(host: String): Future[Seq[ShopInfo]] = usingDB {
    implicit val res = GetResult(r => ShopInfo(
      id = r.<<,
      title = r.<<,
      cityId = r.<<,
      cityName = r.<<,
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
        c.domain = ${host};
    """.as[ShopInfo]
  }

  def update(shop: ShopInfo): Future[Int] = usingDB {
    sql"""
      UPDATE shops
      SET
        title = ${shop.title},
        city_id = ${shop.cityId},
        address = ${shop.address},
        phones = ${shop.phoneNumbers.mkString(";")}
      WHERE
        id = ${shop.id};
    """.as[Int].head
  }

  def add(shop: ShopInfo): Future[Int] = usingDB {
    returningId(sql"""
      insert into
        shops(title, city_id, address, phones)
        values(${shop.title}, ${shop.cityId}, ${shop.address}, ${shop.phoneNumbers.mkString(";")});
    """.as[Int].head)
  }

  def remove(shopId: Int): Future[Int] = usingDB {
    sql"""
      delete from shops where id = ${shopId};
    """.as[Int].head
  }

  //TODO: Check where this data goes
  def getYandexShopInfo = usingDB {
    implicit val getYandexShopInfoResults = GetResult(r => YandexShopInfo(r.<<, r.<<, r.<<))
    sql"""
        select
          title,
          company,
          url
        from yandex_shop_info
        where
          shop_id = 1;
    """.as[YandexShopInfo].head
  }
}
