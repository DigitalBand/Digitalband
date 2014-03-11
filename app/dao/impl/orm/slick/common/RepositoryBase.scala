package dao.impl.orm.slick.common

import play.api.db.DB

import scala.slick.driver.MySQLDriver.simple._

import play.api.Play.current


class RepositoryBase {
  protected lazy val database = Database.forDataSource(DB.getDataSource())

}
