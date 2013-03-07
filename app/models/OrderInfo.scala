package models

class OrderInfo(val deliveryInfo: DeliveryInfo, val items: Seq[CartItem])
