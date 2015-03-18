package dao.impl.orm.slick.common

import slick.driver.JdbcDriver.backend.Database

import play.api.db.DB
import play.api.Play.current


object Profile {

    lazy val database = Database.forDataSource(DB.getDataSource())
}
