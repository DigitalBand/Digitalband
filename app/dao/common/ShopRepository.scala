package dao.common

import models.{YandexShopInfo, ShopInfo}

import scala.concurrent.Future

trait ShopRepository {
  def getYandexShopInfo: Future[YandexShopInfo]
  def remove(shopId: Int): Future[Int]
  def add(shop: ShopInfo): Future[Int]
  def update(shop: ShopInfo): Future[Int]
  def list: Future[Seq[ShopInfo]]
  def get(shopId: Int): Future[ShopInfo]
  def getByCity(cityId: Int): Future[Seq[ShopInfo]]
  def getByHostname(host: String): Future[Seq[ShopInfo]]
}
