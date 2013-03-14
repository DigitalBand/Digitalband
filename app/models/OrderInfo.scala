package models

import org.ocpsoft.prettytime.PrettyTime
import java.sql.Date
import java.util.Locale


class OrderInfo(val id: Int, val orderDate: java.sql.Timestamp,
                val status:String, val deliveryInfo: DeliveryInfo,
                val items: Seq[CartItem]){

  def this(deliveryInfo: DeliveryInfo, items: Seq[CartItem]) =
    this(0, null, "", deliveryInfo, items)

  def this(orderId: Int, orderDate: java.sql.Timestamp, status:String, deliveryInfo:DeliveryInfo) =
    this(orderId, orderDate, status, deliveryInfo, List())

  def orderDateFormatted = {
    new PrettyTime(new Locale("ru")).format(new Date(orderDate.getTime))
  }
}
