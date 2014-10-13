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
        insert into orders (user_id, place_date, name, email, phone, address)
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
          order_items(order_id, product_id, quantity, unit_price)
        select
          ${orderId},
          product_id,
          sum(quantity) as quantity,
          (select price from products where id = si.product_id limit 1) as unit_price
        from
          shopping_items si
        where
          user_id = ${userId}
        group by product_id;
        delete from shopping_items where user_id = ${userId};
       """.execute()
      orderId
    }

  def create(userId: Int, address: DeliveryAddress, personalInfo: PersonalInfo): Int = database withDynSession {
    sqlu"""
      insert into
        orders (user_id, place_date, name, last_name, middle_name, email, phone, delivery_type)
      values(
        $userId,
        ${new Timestamp(new java.util.Date().getTime)},
        ${personalInfo.firstName},
        ${personalInfo.lastName},
        ${personalInfo.middleName},
        ${personalInfo.email},
        ${personalInfo.phone},
        "Доставка");
    """.execute()
    val orderId = sql"select last_insert_id();".as[Int].first
    sqlu"""
        insert into
          order_items(order_id, product_id, quantity, unit_price)
        select
          ${orderId},
          product_id,
          sum(quantity) as quantity,
          (select price from products where id = si.product_id limit 1) as unit_price
        from
          shopping_items si
        where
          user_id = ${userId}
        group by product_id;
        delete from shopping_items where user_id = ${userId};
       """.execute()
    addOrderDeliveryInfo(orderId, "", address)
    orderId
  }

  def addOrderDeliveryInfo(orderId: Int, deliveryType: String, address: DeliveryAddress) = {
//    if(deliveryType == "Доставка")
      sqlu"""
          insert into
            order_delivery_addresses(order_id, city, street, building, housing, apartment)
          values(
            ${orderId},
            ${address.city},
            ${address.street},
            ${address.building},
            ${address.housing},
            ${address.apartment}
          );
         """.execute()
//    else

  }

  def getItems(orderId: Int): Seq[CartItem] = database withDynSession {
    implicit val getCartItem = GetResult(r => new CartItem(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
        select
          o.order_id,
          o.product_id,
          p.title,
          (select image_id from product_images where product_id = o.product_id limit 1) as image_id,
          o.quantity,
          o.unit_price
        from
          order_items o, products p
         where
          o.product_id = p.id and
          o.order_id = $orderId
    """.as[CartItem].list
  }

  def getDeliveryInfo(orderId: Int) = database withDynSession {
    implicit val getDeliveryInfo = GetResult(
      r => new DeliveryInfo(name = r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        name,
        null,
        null
        email,
        phone,
        address
      from
        orders
       where
        id = $orderId
    """.as[DeliveryInfo].first
  }

  override def get(orderId: Int): OrderInfo = database withDynSession {
    implicit val getOrderInfo = GetResult(
      r => new OrderInfo(r.<<, r.<<, r.<<, new DeliveryInfo(name = r.<<, r.<<, r.<<, r.<<)))
    val query = sql"select id, place_date, status, name, email, phone, address from orders where id = $orderId"
    val order = query.as[OrderInfo].first()
    new OrderInfo(order, getItems(orderId))
  }

  def exists(orderId: Int) = database withDynSession {
    sql"""
      select count(order_id) from order_items where order_id = $orderId;
    """.as[Int].first > 0
  }

  def listAll(pageNumber: Int, pageSize: Int): ListPage[OrderInfo] = database withDynSession {
    implicit val getOrderInfo = GetResult(
      r => new OrderInfo(r.<<, r.<<, r.<<, new DeliveryInfo(name = r.<<, r.<<, r.<<, r.<<)))
    val items = sql"""
      select
        id, place_date, status, name, email, phone, address
      from orders
      order by place_date desc
      limit ${pageSize * (pageNumber - 1)}, $pageSize
    """.as[OrderInfo].list
    val totalCount = sql"select count(*) from orders".as[Int].first
    new ListPage[OrderInfo](pageNumber, items, totalCount)
  }

  def changeStatus(orderId: Int, status: String) = database withDynSession {
    sqlu"update orders set status = $status where id = $orderId".execute()
  }

  def delete(orderId: Int) = database withDynSession {
    sqlu"delete from orders where id = $orderId; delete from order_items where order_id = $orderId".execute()
  }

  def getCounters: Seq[(String, Int)] = database withDynSession {
    val query = sql"select status, count(status) as orderCount from orders group by status;"
    query.as[(String, Int)].list
  }

  def countUnconfirmed: Int = database withDynSession {
    sql"select count(*) from orders where status = 'unconfirmed'".as[Int].first
  }

}
