package models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession
import dao.impl.orm.slick.tables.CategoriesTable

case class PictureEntity (val id: Int, val path: String, val imageType: String)




