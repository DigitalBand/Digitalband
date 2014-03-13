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
            shop.userId,
            shop.productId,
            p.title,
            (select image_id from product_images where productId = shop.productId limit 1) as image_id,
            sum(shop.quantity) as quantity,
            p.price
          from shopping_items shop
            left join products p on p.productId = shop.productId
          where shop.userId = $userId
            group by p.productId
      """.as[CartItem].list.filter(p => p.unitPrice > 0)

    }
  }

  def add(item: CartItem): Int = {
    database withDynSession {
      sqlu"""
      insert into shopping_items(productId, userId, quantity, unitPrice)
        select ${item.productId}, ${item.userId}, 0,
        (select price from products where productId = ${item.productId} limit 1)
        from
          dual
        where not exists
          (select * from shopping_items where productId = ${item.productId} and userId = ${item.userId});
        update shopping_items
        set
          quantity = quantity + ${item.count}
        where
          productId = ${item.productId} and userId = ${item.userId};
        """.execute()
      item.userId
    }
  }

  def deleteItem(userId: Int, productId: Int) = database withDynSession {
    sqlu"delete from shopping_items where userId = $userId and productId = $productId".execute()
  }


  def updateItems(userId: Int, items: Seq[CItem]) = {
    database withDynSession {
      def update(citem: Seq[CItem], query: String = ""): String = {
        val item = citem.head
        val mainQuery = s"""
            delete from shopping_items where productId = ${item.productId} and userId = $userId;
            insert into shopping_items(productId, userId, quantity) values (${item.productId}, $userId, ${item.count});
         """
        val combinedQuery = query+mainQuery
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
      update shopping_items set userId = ${authenticatedUserId} where userId = ${anonymousUserId};
    """.execute()
  }
}
