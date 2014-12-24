package dao.common

import models.PageInfo

trait PageRepository {
  def get(pageAlias: String): PageInfo
  def get(pageId: Int): PageInfo
  def list(): Seq[PageInfo]
  def add(page: PageInfo): Int
  def update(page: PageInfo)
  def remove(pageId: Int)
}
