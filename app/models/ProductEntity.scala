package models

import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

case class ProductEntity(title:String, description: String, price: Double, id: Int, defaultImageId: Int)





