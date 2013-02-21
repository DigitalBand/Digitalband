package dao.impl.orm.slick.common
import slick.session.Database
import play.api.db.DB
import play.api.Play.current
import dao.impl.orm.slick.driver.MySQLExtendedDriver

object Profile {
    //Driver configuration
    //TODO: choose right driver based on config value i.e.: db.default.driver=com.mysql.jdbc.Driver
    val driver: MySQLExtendedDriver.type = MySQLExtendedDriver
    lazy val database = Database.forDataSource(DB.getDataSource())
}
