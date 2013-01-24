import dao.impl.orm.slick.tables.CategoriesTable
import play.api.db.DB
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

val database = Database.forDataSource(DB.getDataSource())

for (category <- CategoriesTable.where(_.id === 1)) yield category.id
