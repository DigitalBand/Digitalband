package dao.common

import models.CityInfo

trait CityRepository {
  def get(cityDeliveryId: Int): CityInfo
  def getByHostname(host: String): CityInfo
  def add(cityDelivery: CityInfo): Int
  def update(cityDelivery: CityInfo)
  def remove(cityDeliveryId: Int)
  def list: Seq[CityInfo]
}
