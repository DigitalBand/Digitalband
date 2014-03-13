package dao.impl.orm.slick

import models._
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession

import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import java.sql.Timestamp
import dao.impl.orm.slick.common.RepositoryBase

class OrderRepository extends RepositoryBase with dao.common.OrderRepository {


  def create(deliveryInfo: DeliveryInfo, userId: Int): Int = database withDynSession {
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
          order_items(order_id, product_id, quantity, unitPrice)
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


  def getItems(orderId: Int): Seq[CartItem] = database withDynSession {
    implicit val getCartItem = GetResult(r => new CartItem(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
        select
          o.order_id,
          o.product_id,
          p.title,
          (select imageId from product_images where productId = o.product_id limit 1) as imageId,
          o.quantity,
          o.unitPrice
        from
          order_items o, products p
         where
          o.product_id = p.productId and
          o.order_id = $orderId
    """.as[CartItem].list
  }

  def getDeliveryInfo(orderId: Int) = database withDynSession {
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

  override def get(orderId: Int): OrderInfo = database withDynSession {
    implicit val getOrderInfo = GetResult(r => new OrderInfo(r.<<, r.<<, r.<<, new DeliveryInfo(r.<<, r.<<, r.<<, r.<<)))
    val query = sql"select orderId, placeDate, status, name, email, phone, address from orders where orderId = $orderId"
    val order = query.as[OrderInfo].first()
    new OrderInfo(order, getItems(orderId))
  }

  def exists(orderId: Int) = database withDynSession {
    sql"""
      select count(order_id) from order_items where order_id = $orderId;
    """.as[Int].first > 0
  }

  def listAll(pageNumber: Int, pageSize: Int): ListPage[OrderInfo] = database withDynSession {
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

  def changeStatus(orderId: Int, status: String) = database withDynSession {
    sqlu"update orders set status = $status where orderId = $orderId".execute()
  }

  def delete(orderId: Int) = database withDynSession {
    sqlu"delete from orders where orderId = $orderId; delete from order_items where orderId = $orderId".execute()
  }

  def getCounters: Seq[(String, Int)] = database withDynSession {
    val query = sql"select status, count(status) as orderCount from orders group by status;"
    query.as[(String, Int)].list
  }

  def countUnconfirmed: Int = database withDynSession {
    sql"select count(*) from orders where status = 'unconfirmed'".as[Int].first
  }

}
