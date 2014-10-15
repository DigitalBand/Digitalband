package dao.common

import models.AboutInfo

trait AboutRepository {
  def get(): AboutInfo
  def add(aboutInfo: AboutInfo)
  def update(aboutInfo: AboutInfo)
}

