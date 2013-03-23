package models

class BrandEntity(val id: Int, val title: String, val productCount: Int, val imageId: Int) {
    def this() = this(0, "Unknown", 0, 0)
}
