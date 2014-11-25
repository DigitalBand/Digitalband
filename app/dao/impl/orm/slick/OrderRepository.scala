package dao.impl.orm.slick

import models._
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession

import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import java.sql.Timestamp
import dao.impl.orm.slick.common.RepositoryBase

class OrderRepository extends RepositoryBase with dao.common.OrderRepository {

  def create[TDeliveryInfo](userId: Int, cityId: Option[Int], deliveryInfo: TDeliveryInfo): Int = {
    deliveryInfo match {
      case di: OrderDeliveryInfo => create(di, userId, cityId)
      case pi: PickupDeliveryInfo => create(pi, userId, cityId)
    }
  }

  def create(deliveryInfo: OrderDeliveryInfo, userId: Int, cityId: Option[Int]): Int = database withDynSession {
    val orderId = create(deliveryInfo.personalInfo, userId, cityId, deliveryInfo.comment, "Delivery")
    addOrderItems(orderId, userId)
    addOrderDeliveryInfo(orderId, deliveryInfo.address)
    orderId
  }

  def create(deliveryInfo: PickupDeliveryInfo, userId: Int, cityId: Option[Int]): Int = database withDynSession {
    val orderId = create(deliveryInfo.personalInfo, userId, cityId, deliveryInfo.comment, "Pickup")
    addOrderItems(orderId, userId)
    addOrderPickupInfo(orderId, deliveryInfo.shopId)
    orderId
  }

  def create(personalInfo: PersonalInfo, userId: Int, cityId: Option[Int], comment: String, deliveryType: String): Int = {
    sqlu"""
      insert into
        orders (user_id, city_id, place_date, name, last_name, middle_name, email, phone, comment, delivery_type)
      values(
        $userId,
        $cityId,
        ${new Timestamp(new java.util.Date().getTime)},
        ${personalInfo.firstName},
        ${personalInfo.lastName},
        ${personalInfo.middleName},
        ${personalInfo.email},
        ${personalInfo.phone},
        ${comment},
        ${deliveryType});
    """.execute()
    sql"select last_insert_id();".as[Int].first
  }

  def addOrderItems(orderId: Int, userId: Int) = {
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
  }

  def addOrderDeliveryInfo(orderId: Int, address: DeliveryAddress) = {
      sqlu"""
          insert into
            order_delivery_addresses(order_id, city, street, building, apartment)
          values(
            ${orderId},
            ${address.city},
            ${address.street},
            ${address.building},
            ${address.apartment}
          );
      """.execute()
  }

  def addOrderPickupInfo(orderId: Int, shopId: Int) = {
    sqlu"""
          insert into
            order_delivery_shops(order_id, shop_id)
          values(
            ${orderId},
            ${shopId}
          );
     """.execute()
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
        email,
        phone,
        address
      from
        orders
       where
        id = $orderId
    """.as[DeliveryInfo].first
  }

  def getOrderDeliveryInfo(orderId: Int): OrderDeliveryInfo = database withDynSession {
    implicit val getDeliveryInfo = GetResult(
      r => new OrderDeliveryInfo(
        new DeliveryAddress(city = r.<<, street = r.<<, building = r.<<, apartment = r.<<),
        new PersonalInfo(lastName = r.<<, firstName = r.<<, middleName = r.<<, phone = r.<<, email = r.<<),
        comment = r.<<
      ))
    sql"""
      select
        oda.city,
        oda.street,
        oda.building,
        oda.apartment,
        o.last_name,
        o.name,
        o.middle_name,
        o.phone,
        o.email,
        o.comment
      from
        orders o,
        order_delivery_addresses oda
       where
        o.id = $orderId AND oda.order_id = $orderId
    """.as[OrderDeliveryInfo].first
  }

  def getPickupDeliveryInfo(orderId: Int): PickupDeliveryInfo = database withDynSession {
    implicit val getDeliveryInfo = GetResult(r => new PickupDeliveryInfo(
      shopId = r.<<,
      new PersonalInfo(lastName = r.<<, firstName = r.<<, middleName = r.<<, phone = r.<<, email = r.<<),
      comment = r.<<
    ))
    sql"""
      select
        ods.shop_id,
        o.last_name,
        o.name,
        o.middle_name,
        o.phone,
        o.email,
        o.comment
      from
        orders o,
        order_delivery_shops ods
       where
        o.id = $orderId AND ods.order_id = $orderId
    """.as[PickupDeliveryInfo].first
  }

  override def get(orderId: Int): OrderInfo = database withDynSession {
    implicit val getOrderInfo = GetResult(
      r => new OrderInfo(r.<<, r.<<, r.<<, r.<<, r.<<, new DeliveryInfo(name = r.<<, r.<<, r.<<, r.<<)))
    val query = sql"select id, place_date, status, delivery_type, comment, name, email, phone, address from orders where id = $orderId"
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
      r => new OrderInfo(
        orderId = r.<<,
        orderDate = r.<<,
        status = r.<<,
        deliveryType = r.<<,
        new DeliveryInfo(name = r.<<, email = r.<<, phone = r.<<, address = r.<<)))
    val items = sql"""
      select
        id, place_date, status, delivery_type, Concat(name, ' ', last_name) as name, email, phone, address
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

  def countUnconfirmed = database withDynSession {
    sql"""
      select
        c.domain,
        count(o.id)
      from
        orders o
      left join
        cities c on c.id = o.city_id
      where o.status = 'unconfirmed'
      group by city_id;""".as[(Option[String], Int)].toMap
  }
}
