package dao.impl.orm.slick.common
import slick.session.Database
import play.api.db.DB
import play.api.Play.current
import dao.impl.orm.slick.driver.MySQLExtendedDriver

object Profile {
    val driver: MySQLExtendedDriver.type = MySQLExtendedDriver
    lazy val database = Database.forDataSource(DB.getDataSource())
}
