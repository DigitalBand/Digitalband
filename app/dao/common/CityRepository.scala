package dao.common

import models.{CityShortInfo, CityInfo}

import scala.concurrent.Future

trait CityRepository {
  def get(cityDeliveryId: Int): Future[CityInfo]
  def getByHostname(host: String): Future[CityInfo]
  def add(cityDelivery: CityInfo): Future[Int]
  def update(cityDelivery: CityInfo)
  def remove(cityDeliveryId: Int)
  def list: Future[Seq[CityInfo]]
  def listShortInfo: Future[Seq[CityShortInfo]]
}
