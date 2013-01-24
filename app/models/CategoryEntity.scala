package models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

case class CategoryEntity(id: Int, title: String, imageId: Int = 0, leftValue: Int = 0, rightValue: Int = 0)


