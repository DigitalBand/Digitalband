package models

class ListPage[T](var number: Int, var items: Seq[T], var totalCount: Int) {
  val numbersLimit = 12
  def totalPageCount: Int = {
    val pageSize = items.length
    val rem: Long = totalCount % pageSize
    val pageCount = totalCount / pageSize
    if (rem > 0) pageCount + 1 else pageCount
  }

  def localPageCount: Int = {
    val pageCount = totalPageCount
    val startNumberCount = 6
    if (pageCount <= startNumberCount) pageCount
    if (number != null && number != 0) {
      val numberCount = (startNumberCount + number - 1)
      if (pageCount <= numberCount) pageCount else numberCount
    } else startNumberCount
  }

  def startPage: Int = {
    if (number != null && number != 0) {
      val midValue = (numbersLimit / 2) + 1
      if (number <= midValue) 1 else number - midValue
    } else 1
  }
}
