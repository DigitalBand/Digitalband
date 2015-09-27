package dao.common

import models.PageInfo

import scala.concurrent.Future

trait PageRepository {
  def get(pageAlias: String): Future[PageInfo]
  def get(pageId: Int): Future[PageInfo]
  def list(): Future[Seq[PageInfo]]
  def add(page: PageInfo): Future[Int]
  def update(page: PageInfo): Future[Int]
  def remove(pageId: Int): Future[Int]
}
