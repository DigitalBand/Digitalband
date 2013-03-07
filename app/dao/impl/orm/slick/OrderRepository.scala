package dao.impl.orm.slick

import models._
import common.Profile
import Profile.database
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

class OrderRepository extends dao.common.OrderRepository {
  def create(deliveryInfo: DeliveryInfo, userId: Int): Int = {
    database withSession {
      sqlu"""
        insert into orders (userId, name, email, phone, address)
        values($userId, ${deliveryInfo.name}, ${deliveryInfo.email}, ${deliveryInfo.phone}, ${deliveryInfo.address});
      """.execute()
      val orderId = sql"select last_insert_id();".as[Int].first
      sqlu"""
        insert into
          order_items(orderId, productId, quantity, unitPrice)
        select
          $orderId,
          productId,
          sum(quantity) as quantity,
          (select price from products where productId = si.productId limit 1) as unitPrice
        from
          shopping_items si
        where
          userId = $userId
        group by productId;
        delete from shopping_items where userId = $userId;
       """.execute()
      orderId
    }
  }

  def getItems(orderId: Int): Seq[CartItem] = database withSession {
    implicit val getCartItem = GetResult(r => new CartItem(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
        select
          o.orderId,
          o.productId,
          p.title,
          (select imageId from product_images where productId = o.productId) as imageId,
          o.quantity,
          o.unitPrice
        from
          order_items o, products p
         where
          o.productId = p.productId and
          o.orderId = $orderId
    """.as[CartItem].list
  }

  def get(orderId: Int) = ???
}
