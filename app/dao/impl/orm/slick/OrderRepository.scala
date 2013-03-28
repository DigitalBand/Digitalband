package dao.impl.orm.slick

import models._
import common.Profile
import Profile.database
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import java.sql.Timestamp

class OrderRepository extends dao.common.OrderRepository {


  def create(deliveryInfo: DeliveryInfo, userId: Int): Int = {
    database withSession {
      sqlu"""
        insert into orders (userId, placeDate, name, email, phone, address)
        values(
          $userId,
          ${new Timestamp(new java.util.Date().getTime)},
          ${deliveryInfo.name},
          ${deliveryInfo.email},
          ${deliveryInfo.phone},
          ${deliveryInfo.address});
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
          (select imageId from product_images where productId = o.productId limit 1) as imageId,
          o.quantity,
          o.unitPrice
        from
          order_items o, products p
         where
          o.productId = p.productId and
          o.orderId = $orderId
    """.as[CartItem].list
  }

  def getDeliveryInfo(orderId: Int) = database withSession {
    implicit val getDeliveryInfo = GetResult(r => new DeliveryInfo(r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        name,
        email,
        phone,
        address
      from
        orders
       where
        orderId = $orderId
    """.as[DeliveryInfo].first
  }

  override def get(orderId: Int): OrderInfo = database withSession {
    implicit val getOrderInfo = GetResult(r => new OrderInfo(r.<<, r.<<, r.<<, new DeliveryInfo(r.<<, r.<<, r.<<, r.<<)))
    val query = sql"select orderId, placeDate, status, name, email, phone, address from orders where orderId = $orderId"
    val order = query.as[OrderInfo].first()
    new OrderInfo(order, getItems(orderId))
  }

  def exists(orderId: Int) = database withSession {
    sql"""
      select count(orderId) from order_items where orderId = $orderId;
    """.as[Int].first > 0
  }

  def listAll(pageNumber: Int, pageSize: Int): ListPage[OrderInfo] = database withSession {
    implicit val getOrderInfo = GetResult(r => new OrderInfo(r.<<, r.<<, r.<<, new DeliveryInfo(r.<<, r.<<, r.<<, r.<<)))
    val items = sql"""
      select
        orderId, placeDate, status, name, email, phone, address
      from orders
      order by placeDate desc
      limit ${pageSize * (pageNumber - 1)}, $pageSize
    """.as[OrderInfo].list
    val totalCount = sql"select count(*) from orders".as[Int].first
    new ListPage[OrderInfo](pageNumber, items, totalCount)
  }

  def changeStatus(orderId: Int, status: String) = database withSession {
    sqlu"update orders set status = $status where orderId = $orderId".execute()
  }

  def delete(orderId: Int) = database withSession {
    sqlu"delete from orders where orderId = $orderId; delete from order_items where orderId = $orderId".execute()
  }

  def getCounters: Seq[(String, Int)] = database withSession {
    val query = sql"select status, count(status) as orderCount from orders group by status;"
    query.as[(String, Int)].list
  }

  def countUnconfirmed: Int = database withSession {
    sql"select count(*) from orders where status = 'unconfirmed'".as[Int].first
  }

}
