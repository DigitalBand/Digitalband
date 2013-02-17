package dao.impl.orm.squeryl.common.tables

case class CategoryTable(
       categoryId: Int,
       title: String,
       parentCategoryId: Option[Int],
       leftValue: Int,
       rightValue: Int)
