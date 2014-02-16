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
    sql"""select last_insert_id();""".as[Int].first
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
    sqlu"""
      delete from stock_items where id = ${stockItemId};
    """.execute()
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
  }
}
