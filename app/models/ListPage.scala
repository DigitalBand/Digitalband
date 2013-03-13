package models

class ListPage[T](val number: Int, val items: Seq[T], val totalCount: Int)
