package dao.impl.orm.slick.common

import play.api.db.DB

import slick.driver.MySQLDriver.api._

import play.api.Play.current


class RepositoryBase {
  protected lazy val database = Database.forDataSource(DB.getDataSource())
  def getDB = Database.forDataSource(DB.getDataSource())
  def usingDB[T](f: => DBIOAction[T, NoStream, Nothing]) = {
    val db = getDB
    try {
      db.run(f)
    } finally db.close()
  }
}
