package models

import org.joda.time.DateTime

class OrderInfo(val id: Int, val orderDate: DateTime,
                val status:String, val deliveryInfo: DeliveryInfo,
                val items: Seq[CartItem]){
  def this(deliveryInfo: DeliveryInfo, items: Seq[CartItem]) =
    this(0, DateTime.now(), "", deliveryInfo, items)
  def this(orderId: Int, orderDate: DateTime, status:String, deliveryInfo: DeliveryInfo) =
    this(orderId, orderDate, status, deliveryInfo, List())
}
