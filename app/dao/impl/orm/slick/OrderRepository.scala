package dao.impl.orm.slick

import models._
import slick.dbio.{NoStream, DBIOAction}
import slick.jdbc.GetResult
import slick.driver.MySQLDriver.api._
import java.sql.Timestamp
import dao.impl.orm.slick.common.RepositoryBase
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrderRepository extends RepositoryBase with dao.common.OrderRepository {

  def create[TDeliveryInfo](userId: Int, cityId: Option[Int], deliveryInfo: TDeliveryInfo): Future[Int] = {
    deliveryInfo match {
      case di: OrderDeliveryInfo => create(di, userId, cityId)
      case pi: PickupDeliveryInfo => create(pi, userId, cityId)
    }
  }

  def create(deliveryInfo: OrderDeliveryInfo, userId: Int, cityId: Option[Int]): Future[Int] = {
    for {
      orderId <- create(deliveryInfo.personalInfo, userId, cityId, deliveryInfo.comment, "Delivery")
      orderItemsCount <- addOrderItems(orderId, userId)
      recordsCount <- addOrderDeliveryInfo(orderId, deliveryInfo.address)
    } yield orderId
  }

  def create(deliveryInfo: PickupDeliveryInfo, userId: Int, cityId: Option[Int]): Future[Int] = {
    for {
      orderId <- create(deliveryInfo.personalInfo, userId, cityId, deliveryInfo.comment, "Pickup")
      orderItemsCount <- addOrderItems(orderId, userId)
      recordsCount <- addOrderPickupInfo(orderId, deliveryInfo.shopId)
    } yield orderId
  }

  def create(personalInfo: PersonalInfo, userId: Int, cityId: Option[Int], comment: String,
             deliveryType: String): Future[Int] = usingDB {
    returningId(sql"""
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
      """.as[Int].head)
  }

  def addOrderItems(orderId: Int, userId: Int): Future[Int] = usingDB {
    sql"""
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
       """.as[Int].head
  }

  def addOrderDeliveryInfo(orderId: Int, address: DeliveryAddress): Future[Int] = usingDB {
      sql"""
          insert into
            order_delivery_addresses(order_id, city, street, building, apartment)
          values(
            ${orderId},
            ${address.city},
            ${address.street},
            ${address.building},
            ${address.apartment}
          );
      """.as[Int].head
  }

  def addOrderPickupInfo(orderId: Int, shopId: Int): Future[Int] = usingDB {
    sql"""
          insert into
            order_delivery_shops(order_id, shop_id)
          values(
            ${orderId},
            ${shopId}
          );
     """.as[Int].head
  }

  def getItems(orderId: Int): Future[Seq[CartItem]] = usingDB {
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
    """.as[CartItem]
  }

  def getDeliveryInfo(orderId: Int): Future[DeliveryInfo] = usingDB {
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
    """.as[DeliveryInfo].head
  }

  def getOrderDeliveryInfo(orderId: Int): Future[OrderDeliveryInfo] = usingDB {
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
    """.as[OrderDeliveryInfo].head
  }

  def getPickupDeliveryInfo(orderId: Int): Future[PickupDeliveryInfo] = usingDB {
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
    """.as[PickupDeliveryInfo].head
  }

  override def get(orderId: Int): Future[OrderInfo] = {
    val orderFuture = usingDB {
      implicit val getOrderInfo = GetResult(
        r => new OrderInfo(r.<<, r.<<, r.<<, r.<<, r.<<, new DeliveryInfo(name = r.<<, r.<<, r.<<, r.<<)))
      sql"""
        select
          id, place_date, status, delivery_type, comment, name, email, phone, address
        from
          orders
        where id = $orderId
      """.as[OrderInfo].head
    }
    for {
      order <- orderFuture
      items <- getItems(orderId)
    } yield new OrderInfo(order, items)
  }

  def exists(orderId: Int): Future[Boolean] = {
    usingDB {
      sql"""
        select
          count(order_id)
        from
          order_items
        where
          order_id = $orderId;
      """.as[Int].head
    }.map(count => count > 0)
  }

  def listAll(pageNumber: Int, pageSize: Int): Future[ListPage[OrderInfo]] = {
    implicit val getOrderInfo = GetResult(
      r => new OrderInfo(
        orderId = r.<<,
        orderDate = r.<<,
        status = r.<<,
        deliveryType = r.<<,
        new DeliveryInfo(name = r.<<, email = r.<<, phone = r.<<, address = r.<<)))
    val orderInfoListFuture = usingDB {
      sql"""
        select
          id, place_date, status, delivery_type, Concat(name, ' ', last_name) as name, email, phone, address
        from
          orders
        order by id desc
        limit ${pageSize * (pageNumber - 1)}, $pageSize
      """.as[OrderInfo]
    }
    val totalCountFuture = usingDB {
      sql"select count(*) from orders".as[Int].head
    }
    for {
      items <- orderInfoListFuture
      totalCount <- totalCountFuture
    } yield new ListPage[OrderInfo](pageNumber, items, totalCount)
  }

  def changeStatus(orderId: Int, status: String): Future[Int] = usingDB {
    sql"update orders set status = $status where id = $orderId".as[Int].head
  }

  def delete(orderId: Int): Future[Int] = usingDB {
    sql"delete from orders where id = $orderId; delete from order_items where order_id = $orderId".as[Int].head
  }

  def getCounters: Future[Seq[(String, Int)]] = usingDB {
    val query = sql"select status, count(status) as orderCount from orders group by status;"
    query.as[(String, Int)]
  }

  def groupUnconfirmedByHost: Future[Map[Option[String], Int]] = usingDB {
    sql"""
      select
        c.domain,
        count(o.id)
      from
        orders o
      left join
        cities c on c.id = o.city_id
      where o.status = 'unconfirmed'
      group by city_id;
    """.as[(Option[String], Int)]
  }.map(list => list.toMap)
}
