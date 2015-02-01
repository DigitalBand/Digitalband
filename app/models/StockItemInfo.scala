package models
import play.api.libs.json._

case class StockItemInfo(id: Int, quantity: Int, dealerName: String, dealerPrice: Double, shopId: Int, shopTitle: String)
object StockItemInfo {
  implicit val reads = Json.reads[StockItemInfo]
  implicit val writes = Json.writes[StockItemInfo]
}
