package helpers

case class PagerHelper(val pageSize: Int, val totalCount: Int, val number: Int) {
  val numbersLimit = 7
  def totalPageCount: Int = {
    val rem: Long = totalCount % pageSize
    val pageCount = totalCount / pageSize
    if (rem > 0) pageCount + 1 else pageCount
  }

  def localPageCount: Int = {
    val pageCount = totalPageCount
    val startNumberCount = 4
    if (pageCount <= startNumberCount) pageCount
    if (number > 0) {
      val numberCount = (startNumberCount + number - 1)
      if (pageCount <= numberCount) pageCount else numberCount
    } else startNumberCount
  }

  def startPage: Int = {
    if (number > 0) {
      val midValue = (numbersLimit / 2) + 1
      if (number <= midValue) 1 else number - midValue
    } else 1
  }
}
