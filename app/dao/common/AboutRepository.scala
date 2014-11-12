package dao.common

import models.AboutInfo

trait AboutRepository {
  def get(): AboutInfo
  def save(aboutInfo: AboutInfo)
}

