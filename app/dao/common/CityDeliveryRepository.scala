package dao.common

import models.CityDeliveryInfo

trait CityDeliveryRepository {
  def get(cityDeliveryId: Int): CityDeliveryInfo
  def add(cityDelivery: CityDeliveryInfo): Int
  def update(cityDelivery: CityDeliveryInfo)
  def remove(cityDeliveryId: Int)
  def list: Seq[CityDeliveryInfo]
}
