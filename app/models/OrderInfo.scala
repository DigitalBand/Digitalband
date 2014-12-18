package models

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

import org.joda.time.DateTime
import org.ocpsoft.prettytime.PrettyTime
import java.sql.Date
import java.util.Locale


class OrderInfo(val id: Int, val orderDate: java.sql.Timestamp,
                val status: String, val deliveryType: String, val comment: String,
                val deliveryInfo: DeliveryInfo,
                val items: Seq[CartItem]){

  def this(id: Int, deliveryInfo: DeliveryInfo, items: Seq[CartItem]) =
    this(id, null, "", "", "", deliveryInfo, items)

  def this(orderId: Int, orderDate: java.sql.Timestamp, status: String, deliveryInfo:DeliveryInfo) =
    this(orderId, orderDate, status, "", "", deliveryInfo, List())

  def this(orderId: Int, orderDate: java.sql.Timestamp, status: String, deliveryType: String, deliveryInfo:DeliveryInfo) =
    this(orderId, orderDate, status, deliveryType, "", deliveryInfo, List())

  def this(orderId: Int, orderDate: java.sql.Timestamp, status: String, comment: String, deliveryType: String, deliveryInfo:DeliveryInfo) =
    this(orderId, orderDate, status, deliveryType, comment, deliveryInfo, List())

  def this(order: OrderInfo, items: Seq[CartItem]) =
    this(order.id, order.orderDate, order.status, order.comment, order.deliveryType, order.deliveryInfo, items)

  def orderDateFormatted = {
    val dt1 = new SimpleDateFormat("dd/MM/yyyy HH:mm")
    dt1.format(new Date(orderDate.getTime))
  }
}
