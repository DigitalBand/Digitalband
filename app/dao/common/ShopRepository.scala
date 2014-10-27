package dao.common

import models.{YandexShopInfo, ShopInfo}

trait ShopRepository {
  def getYandexShopInfo: YandexShopInfo

  def remove(shopId: Int)
  def add(shop: ShopInfo): Int
  def update(shop: ShopInfo)
  def list: Seq[ShopInfo]
  def get(shopId: Int): ShopInfo
  def getByCity(cityId: Int): Seq[ShopInfo]
  def getByHostname(host: String): Seq[ShopInfo]
}
