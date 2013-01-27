package models

case class CategoryEntity(id: Int, title: String, imageId: Int = 0, leftValue: Int = 0, rightValue: Int = 0, parentId: Int = 0)

case class CategoryListItem(id: Int, title: String, productCount: Int)


