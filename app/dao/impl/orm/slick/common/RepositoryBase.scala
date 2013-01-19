package dao.impl.orm.slick.common

import play.api.db.DB
import slick.session.Database
import play.api.Play.current

class RepositoryBase {
  protected lazy val database = Database.forDataSource(DB.getDataSource())

}
