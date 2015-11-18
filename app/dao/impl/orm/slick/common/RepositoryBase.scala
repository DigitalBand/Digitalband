package dao.impl.orm.slick.common

import play.api.db.DB
import slick.dbio.{NoStream, DBIOAction}
import slick.driver.MySQLDriver.api._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

class RepositoryBase {
  protected lazy val database = Database.forDataSource(DB.getDataSource())

  def getDB = Database.forDataSource(DB.getDataSource())

  def usingDB[T](f: => DBIOAction[T, NoStream, Nothing]) = {
    val db = getDB
    db.run(f)
  }

  def returningId(insertStatement: DBIOAction[Int, NoStream, Effect]) = {
    DBIO.sequence(Seq(
      insertStatement,
      sql"""select last_insert_id();""".as[Int].head
    )).map(list => list.last)
  }
}
