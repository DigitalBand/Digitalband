package models

class ListPage[T](var number: Int, var items: Seq[T], var totalCount: Int)
