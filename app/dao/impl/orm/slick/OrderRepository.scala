package dao.impl.orm.slick

import models._
import common.Profile
import Profile.database
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}

class OrderRepository extends dao.common.OrderRepository {
  def create(deliveryInfo: DeliveryInfo, cartId: Int, userId: Int): Int = {
    database withSession {
      val insertQuery = Q.updateNA(s"""
        SET SQL_SAFE_UPDATES=0;
        update
          shopping_items as si
        set
          unitPrice = (select price from products where productId = si.productId limit 1)
        where cartId = $cartId;

        insert into orders (userId) values($userId);
       """)
      insertQuery.execute()
      val getOrderId = Q.queryNA[Int]("select max(orderId) from orders;")
      val orderId = getOrderId.first()
      val query = Q.updateNA(s"""
        insert into order_items(orderId, productId, quantity, unitPrice)

        select $orderId, productId, sum(quantity) as quantity, unitPrice from shopping_items
        where cartId = $cartId
        group by productId;

        delete from shopping_items where cartId = $cartId;
       """)
      query.execute()
      orderId
    }
  }

  def getItems(orderId: Int): Seq[CartItem] = database withSession {
    implicit val getCartItem = GetResult(r => new CartItem(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    val query = Q.queryNA[CartItem]( s"""
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
      """)
    val statement = query.getStatement
    query.list
  }
}
