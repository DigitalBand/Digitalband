package dao.common

import models.AboutInfo

trait AboutRepository {
  def get(): Option[AboutInfo]
  def save(aboutInfo: AboutInfo)
}

