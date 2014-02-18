package dao.impl.orm.slick

import dao.impl.orm.slick.common.Profile
import Profile.database._
import Profile.driver.simple._
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

import slick.jdbc.{StaticQuery => Q, GetResult}
import models.StockItemInfo

class StockItemRepository extends dao.common.StockItemRepository {
  def create(productId: Int, stockItem: StockItemInfo): Int = withSession {
    sqlu"""
      insert into
        stock_items(product_id, dealer_id, dealer_price)
        values(${productId}, (select id from dealers where title = ${stockItem.dealerName}), ${stockItem.dealerPrice})
    """.execute()
    val result = sql"""select last_insert_id();""".as[Int].first
    cacheStock(productId)
    result
  }

  def cacheStock(productId: Int) = withSession {
    sqlu"""
      update
        products
      set
        isAvailable = ((select count(*) from stock_items where product_id = ${productId}) > 0)
      where
        productId = ${productId}
    """.execute()
  }
  def list(productId: Int) = withSession {
    implicit val res = GetResult(r => StockItemInfo(r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        s.id,
        s.quantity,
        (select title from dealers where id = s.dealer_id) as dealerName,
        s.dealer_price
      from stock_items s
      where
        s.product_id = ${productId};
    """.as[StockItemInfo].list
  }

  def remove(stockItemId: Int) = withSession {
    val productId = getProductIdByStock(stockItemId)
    sqlu"""
      delete from stock_items where id = ${stockItemId};
    """.execute()
    cacheStock(productId)

  }

  def getProductIdByStock(stockItemId: Int) = withSession {
    val count = sql"""
      select count(product_id) from stock_items where id = ${stockItemId};
    """.as[Int].first
    if (count > 0) {
    sql"""
      select product_id from stock_items where id = ${stockItemId};
    """.as[Int].first
    } else {
      0
    }
  }

  def update(stockItem: StockItemInfo): Unit = withSession {
    sqlu"""
      update
        stock_items
      set
        quantity = ${stockItem.quantity},
        dealer_price = ${stockItem.dealerPrice},
        dealer_id = (select id from dealers where title = ${stockItem.dealerName})
      where
        id = ${stockItem.id}
    """.execute()
    val productId = getProductIdByStock(stockItem.id)
    cacheStock(productId)
  }
}
