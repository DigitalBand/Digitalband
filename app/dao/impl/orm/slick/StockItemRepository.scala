package dao.impl.orm.slick

import dao.impl.orm.slick.common.{RepositoryBase}
import scala.slick.driver.JdbcDriver.simple._
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation

import models.StockItemInfo

class StockItemRepository extends RepositoryBase with dao.common.StockItemRepository {
  def create(productId: Int, stockItem: StockItemInfo): Int = database withDynSession {
    sqlu"""
      insert into
        stock_items(product_id, dealer_id, dealer_price, shop_id)
        values(${productId}, (select id from dealers where title = ${stockItem.dealerName}), ${stockItem.dealerPrice}, ${stockItem.shopId})
    """.execute()
    val result = sql"""select last_insert_id();""".as[Int].first
    cacheStock(productId)
    result
  }

  def cacheStock(productId: Int) = database withDynSession {
    sqlu"""
      update
        products
      set
        is_available = ((select sum(quantity) from stock_items where product_id = ${productId}) > 0)
      where
        id = ${productId}
    """.execute()
  }
  def list(productId: Int) = database withDynSession {
    implicit val res = GetResult(r => StockItemInfo(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        s.id,
        s.quantity,
        (select title from dealers where id = s.dealer_id) as dealerName,
        s.dealer_price,
        sh.id,
        sh.title
      from stock_items s
      inner join shops sh on sh.id = s.shop_id
      where
        s.product_id = ${productId};
    """.as[StockItemInfo].list
  }

  def remove(stockItemId: Int) = database withDynSession {
    val productId = getProductIdByStock(stockItemId)
    sqlu"""
      delete from stock_items where id = ${stockItemId};
    """.execute()
    cacheStock(productId)

  }

  def getProductIdByStock(stockItemId: Int) = database withDynSession {
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

  def update(stockItem: StockItemInfo): Unit = database withDynSession {
    sqlu"""
      update
        stock_items
      set
        quantity = ${stockItem.quantity},
        dealer_price = ${stockItem.dealerPrice},
        dealer_id = (select id from dealers where title = ${stockItem.dealerName}),
        shop_id = ${stockItem.shopId}
      where
        id = ${stockItem.id}
    """.execute()
    val productId = getProductIdByStock(stockItem.id)
    cacheStock(productId)
  }
}
