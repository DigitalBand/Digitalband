package dao.impl.orm.slick

import common.Profile
import models.{CItem, BrandEntity, CartItem}
import Profile.database
import Profile.driver.simple._
import Database.threadLocalSession


import slick.jdbc.{StaticQuery => Q, GetResult}

class CartRepository extends dao.common.CartRepository {
  def list(cartId: Int): Seq[CartItem] = {
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
          c.cartId = $cartId
      """)
      query.list.filter(p => p.unitPrice > 0)
    }
  }

  def add(item: CartItem): Int = {
    database withSession {
      val cartId = item.cartId
      val query = Q.updateNA( s"""
      insert into shopping_items(productId, cartId, quantity, unitPrice)
        select ${item.productId}, ${cartId}, 0,
        (select price from products where productId = ${item.productId} limit 1)
        from
          dual
        where not exists
          (select * from shopping_items where productId = ${item.productId} and cartId = ${cartId});
        update shopping_items
        set
          quantity = quantity + ${item.count}
        where
          productId = ${item.productId} and cartId = ${cartId};
        update
          cart
        set
          updateDate = CURRENT_TIMESTAMP
        where cartId = $cartId;
        """)
      query.execute()

      cartId
    }
  }

  def createCart(userId: Int = 0): Int = {
    database withSession {
      Q.updateNA(s"insert into cart(userId) values($userId);").execute()
      val query = Q.queryNA[Int]("select max(cartId) from cart;")
      query.first()
    }
  }

  def deleteItem(cartId: Int, productId: Int) = {
    database withSession {
      Q.updateNA(s"delete from shopping_items where cartId = $cartId and productId = $productId").execute()
    }
  }

  def updateItems(cartId: Int, items: Seq[CItem]) = {
    database withSession {
      def update(citem: Seq[CItem], query: String = ""): String = {
        val item = citem.head
        if (citem.tail.length > 0)
          update(citem.tail,
            query + s"update shopping_items set quantity = ${item.count} where productId = ${item.productId} and cartId = $cartId;")
        else
          query + s"update shopping_items set quantity = ${item.count} where productId = ${item.productId} and cartId = $cartId;"
      }
      val query = update(items)
      Q.updateNA(query).execute()
    }
  }
}
