package dao.impl.orm.slick

import slick.jdbc.GetResult
import slick.driver.JdbcDriver.api._

import models.{CItem, CartItem}
import dao.impl.orm.slick.common.RepositoryBase
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CartRepository extends RepositoryBase with dao.common.CartRepository {
  def list(userId: Int): Future[Seq[CartItem]] = usingDB {
      implicit val getCartItem = GetResult(r => new CartItem(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
      sql"""
         select
            shop.user_id,
            shop.product_id,
            p.title,
            (select image_id from product_images where product_id = shop.product_id limit 1) as image_id,
            sum(shop.quantity) as quantity,
            p.price
          from shopping_items shop
            left join products p on p.id = shop.product_id
          where
            shop.user_id = ${userId} and p.price > 0
          group by
            p.id
      """.as[CartItem]
    }

  def add(item: CartItem): Future[Int] = usingDB {
      sqlu"""
        INSERT INTO shopping_items(product_id, user_id, quantity, unit_price)
          SELECT ${item.productId}, ${item.userId}, 0,
          (SELECT price FROM products WHERE id = ${item.productId} LIMIT 1)
          FROM
            dual
          WHERE NOT exists
            (SELECT * FROM shopping_items WHERE product_id = ${item.productId} AND user_id = ${item.userId});
          UPDATE shopping_items
          SET
            quantity = quantity + ${item.count}
          WHERE
            product_id = ${item.productId} AND user_id = ${item.userId};
      """
  } map { insertedCount => item.userId }



  def deleteItem(userId: Int, productId: Int): Future[Int] = usingDB {
    sql"DELETE FROM shopping_items WHERE user_id = ${userId} AND product_id = ${productId}".as[Int].head
  }

  def updateItems(userId: Int, items: Seq[CItem]) = {
    def updateQuery(item: CItem): DBIO[Int] = sql"""
      DELETE FROM shopping_items WHERE product_id = ${item.productId} AND user_id = ${userId};
      INSERT INTO shopping_items(product_id, user_id, quantity) VALUES (${item.productId}, ${userId}, ${item.count});
    """.as[Int].head
    usingDB {
      def update(citem: Seq[CItem], updates: Seq[DBIO[Int]])
      : Seq[DBIO[Int]] = {
        val combinedQuery = updates :+ updateQuery(citem.head)
        if (citem.tail.nonEmpty)
          update(citem.tail, combinedQuery)
        else
          combinedQuery
      }
      val query = update(items, Nil)
      DBIO.sequence(query).map(_.sum)
    }
  }

  def mergeShoppingCarts(authenticatedUserId: Int, anonymousUserId: Int) = usingDB {
    sql"""
      UPDATE shopping_items SET user_id = ${authenticatedUserId} WHERE user_id = ${anonymousUserId};
    """.as[Int].head
  }
}
