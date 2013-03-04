package dao.impl.orm.slick

import common.Profile
import models.{CItem, BrandEntity, CartItem}
import Profile.database
import Profile.driver.simple._
import Database.threadLocalSession


import slick.jdbc.{StaticQuery => Q, GetResult}

class CartRepository extends dao.common.CartRepository {
  def list(userId: Int): Seq[CartItem] = {
    database withSession {
      implicit val getCartItem = GetResult(r => new CartItem(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
      val query = Q.queryNA[CartItem]( s"""
        select
          c.cartId,
          c.productId,
          p.title,
          (select imageId from product_images where productId = c.productId) as imageId,
          c.quantity,
          p.price
        from
          shopping_items c, products p
         where
          c.productId = p.productId and
          c.userId = $userId
      """)
      query.list.filter(p => p.unitPrice > 0)
    }
  }

  def add(item: CartItem): Int = {
    database withSession {
      val query = Q.updateNA( s"""
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
        update
          cart
        set
          updateDate = CURRENT_TIMESTAMP
        where userId = ${item.userId};
        """)
      query.execute()

      item.userId
    }
  }

  def deleteItem(userId: Int, productId: Int) = {
    database withSession {
      Q.updateNA(s"delete from shopping_items where userId = $userId and productId = $productId").execute()
    }
  }

  def updateItems(userId: Int, items: Seq[CItem]) = {
    database withSession {
      def update(citem: Seq[CItem], query: String = ""): String = {
        val item = citem.head
        if (citem.tail.length > 0)
          update(citem.tail,
            query + s"update shopping_items set quantity = ${item.count} where productId = ${item.productId} and userId = $userId;")
        else
          query + s"update shopping_items set quantity = ${item.count} where productId = ${item.productId} and userId = $userId;"
      }
      val query = update(items)
      Q.updateNA(query).execute()
    }
  }
}
