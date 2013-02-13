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
      val query = Q.queryNA[Int](s"""
        SET SQL_SAFE_UPDATES=0;
        update
          shopping_items as si
        set
          unitPrice = (select price from products where productId = si.productId limit 1)
        where cartId = $cartId;

        insert into orders (userId) values(${userId});
        set @orderId := (select last_insert_id());
        insert into order_items(orderId, productId, quantity, unitPrice)
        select @orderId, productId, sum(quantity) as quantity, unitPrice from shopping_items
        where cartId = ${cartId}
        group by productId;
        delete from shopping_items where cartId = ${cartId};
        select @orderId;
       """)
      query.first
    }
  }

  def getItems(orderId: Int): Seq[CartItem] = {
    implicit val getCartItem = GetResult(r => new CartItem(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    val query = Q.queryNA[CartItem]( s"""
        select
          c.cartId,
          c.productId,
          p.title,
          (select imageId from product_images where productId = c.productId) as imageId,
          c.quantity,
          c.unitPrice
        from
          order_items c, products p
         where
          c.productId = p.productId and
          c.orderId = $orderId
      """)
    query.list
  }
}
