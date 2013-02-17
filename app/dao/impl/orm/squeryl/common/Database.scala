package dao.impl.orm.squeryl.common

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import tables._

object Database extends Schema {
  val categories = table[CategoryTable]("categories")
  val categoryImages = table[CategoryImages]("category_images")
  val products = table[Products]("products")
}
