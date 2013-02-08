package dao.impl.orm.slick.common
import slick.driver.ExtendedProfile
import slick.session.Database
import play.api.db.DB
import play.api.Play.current

object Profile {
    //Driver configuration
    //TODO: choose right driver based on config value i.e.: db.default.driver=com.mysql.jdbc.Driver
    val driver: ExtendedProfile = scala.slick.driver.MySQLDriver
    lazy val database = Database.forDataSource(DB.getDataSource())
}
