package webServices.common

import models.{ListPage, ImageSearchItem}
import concurrent.Future

trait ImageSearch {
  def getList[S](search: String, pageNumber: Int)(f: ListPage[ImageSearchItem] => S): Future[S]

}
