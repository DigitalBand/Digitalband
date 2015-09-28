package dao.impl.orm.slick

import dao.impl.orm.slick.common.RepositoryBase
import slick.driver.JdbcDriver.api._
import slick.jdbc.GetResult
import helpers.PhoneHelper.parsePhones
import models.{ShopListItem, StockItemInfo}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class StockItemRepository extends RepositoryBase with dao.common.StockItemRepository {
  def create(productId: Int, stockItem: StockItemInfo): Future[Int] = {
    val result = usingDB {
      sql"""
      insert into
        stock_items(product_id, dealer_id, dealer_price, shop_id, quantity)
        values(${productId}, (select id from dealers where title = ${stockItem.dealerName}), ${stockItem.dealerPrice}, ${stockItem.shopId}, ${stockItem.quantity});
        select last_insert_id();
    """.as[Int].head
    }
    cacheStock(productId)
    result
  }

  def cacheStock(productId: Int): Future[Int] = usingDB {
    sql"""
      update
        products
      set
        is_available = ((select sum(quantity) from stock_items where product_id = ${productId}) > 0)
      where
        id = ${productId}
    """.as[Int].head
  }

  def list(productId: Int) = usingDB {
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
    """.as[StockItemInfo]
  }

  def remove(stockItemId: Int): Future[Int] = {
    val updateCountFuture = usingDB {
      sql"""
        delete from stock_items where id = ${stockItemId};
      """.as[Int].head
    }
    for {
      productId <- getProductIdByStock(stockItemId)
      updateCount <- updateCountFuture
      cachedCount <- cacheStock(productId)
    } yield {
      updateCount
    }
  }

  def getProductIdByStock(stockItemId: Int): Future[Int] = {
    val stockItemsCountFuture = usingDB {
      sql"""
        select count(product_id) from stock_items where id = ${stockItemId};
      """.as[Int].head
    }
    val productIdFuture = usingDB {
      sql"""
        select product_id from stock_items where id = ${stockItemId};
      """.as[Int].head
    }
    for {
      stockItemsCount <- stockItemsCountFuture
      productId <- productIdFuture if stockItemsCount > 0
    } yield {
      if (stockItemsCount > 0) {
        productId
      } else {
        0
      }
    }
  }

  def update(stockItem: StockItemInfo): Future[Int] = {
    val updatedCountFuture = usingDB {
      sql"""
        update
          stock_items
        set
          quantity = ${stockItem.quantity},
          dealer_price = ${stockItem.dealerPrice},
          dealer_id = (select id from dealers where title = ${stockItem.dealerName}),
          shop_id = ${stockItem.shopId}
        where
          id = ${stockItem.id}
      """.as[Int].head
    }
    for {
      productId <- getProductIdByStock(stockItem.id)
      cachedCount <- cacheStock(productId)
      updatedCount <- updatedCountFuture
    } yield {
        updatedCount
    }
  }

  def shopList(productId: Int) = usingDB {
    implicit val getResult = GetResult(r => ShopListItem(r.<<, r.<<, parsePhones(r.<<), r.<<))
    sql"""
      select
        s.id,
        s.title,
        s.phones,
        sum(si.quantity) as quantity
      from
        stock_items si
        inner join shops s on s.id = si.shop_id
      where
        product_id = ${productId} group by si.shop_id;
    """.as[ShopListItem]
  }
}
