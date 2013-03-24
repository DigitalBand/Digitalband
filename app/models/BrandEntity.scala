package models

class BrandEntity(val id: Int, val title: String, val productCount: Int, val imageId: Int) {
  def this() = this(0, "Unknown", 0, 0)

  def this(id: Int, title: String) = this(id, title, 0, 0)
  def this(title: String) = this(0, title)
}
