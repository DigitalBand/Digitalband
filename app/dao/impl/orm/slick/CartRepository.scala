package dao.impl.orm.slick

import common.Profile
import models.{BrandEntity, CartItem}
import Profile.database
import Profile.driver.simple._
import Database.threadLocalSession


import slick.jdbc.{StaticQuery => Q, GetResult}

class CartRepository extends dao.common.CartRepository {
  def list(cartId: Int): Seq[CartItem] = {
    database withSession {
      implicit val getCartItem = GetResult(r => new CartItem(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
      val query = Q.queryNA[CartItem](s"""
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
      query.list
    }
  }

  def add(item: CartItem): Int = {
    database withSession {
      val cartId = item.cartId match {
        case 0 => createCart
        case x => x
      }
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
        """)
      query.execute()

      cartId
    }
  }

  private def createCart: Int = {
    database withSession {
      Q.updateNA("insert into cart() values();").execute()
      val query = Q.queryNA[Int]("select max(cartId) from cart;")
      query.first()
    }
  }
}
