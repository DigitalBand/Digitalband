package dao.impl.orm.slick

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession


import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

import models.{CItem, CartItem}
import dao.impl.orm.slick.common.RepositoryBase

class CartRepository extends RepositoryBase with dao.common.CartRepository {
  def list(userId: Int): Seq[CartItem] = {
    database withDynSession {
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
          where shop.user_id = ${userId}
            group by p.id
      """.as[CartItem].list.filter(p => p.unitPrice > 0)

    }
  }

  def add(item: CartItem): Int = {
    database withDynSession {
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
        """.execute()
      item.userId
    }
  }

  def deleteItem(userId: Int, productId: Int) = database withDynSession {
    sqlu"DELETE FROM shopping_items WHERE user_id = ${userId} AND product_id = ${productId}".execute()
  }

  def updateItems(userId: Int, items: Seq[CItem]) = {
    database withDynSession {
      def update(citem: Seq[CItem], query: String = ""): String = {
        val item = citem.head
        val mainQuery = s"""
            DELETE FROM shopping_items WHERE product_id = ${item.productId} AND user_id = ${userId};
            INSERT INTO shopping_items(product_id, user_id, quantity) VALUES (${item.productId}, ${userId}, ${item.count});
         """
        val combinedQuery = query + mainQuery
        if (citem.tail.length > 0)
          update(citem.tail, combinedQuery)
        else
          combinedQuery
      }
      val query = update(items)
      Q.updateNA(query).execute()
    }
  }

  def mergeShoppingCarts(authenticatedUserId: Int, anonymousUserId: Int) = database withDynSession {
    sqlu"""
      UPDATE shopping_items SET user_id = ${authenticatedUserId} WHERE user_id = ${anonymousUserId};
    """.execute()
  }
}
