package webServices
import models.{ImageSearchItem, ListPage}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsArray
import play.api.libs.ws.WS
import webServices.common.ImageSearch
import play.api.Play.current
import scala.concurrent.Future

class GoogleImageSearch extends ImageSearch {
  def getList[S](search: String, pageNumber: Int)(f: ListPage[ImageSearchItem] => S): Future[S] = {
    val num = 10
    val offset = (pageNumber - 1) * num + 1
    val key = Vector("AIzaSyAHyhiLNqUOfhcf362AoEEPl6s65UhzbMg", "AIzaSyDByv-9nh9XS9RpLSMUgqj7z6iUejzG8nY")(1)
    val cx = Vector("009346898408990037447:evnb2u_eytw", "006243113112137199630:y3v5ecyacm8")(1)
    val searchEncoded = java.net.URLEncoder.encode(search, "UTF-8")
    val params = s"key=$key&cx=$cx&q=$searchEncoded&searchType=image&start=$offset&num=$num"
    val url = s"https://www.googleapis.com/customsearch/v1?$params"
    WS.url(url).get().map { response =>
      val totalResults = (response.json \ "searchInformation" \ "totalResults").as[String].toInt match {
        case x if x > 100 => 100
        case x => x
      }
      val items = (response.json \\ "items").head.as[JsArray].value
      val images = items.map {
        js =>
          new ImageSearchItem(
            (js \ "image" \ "thumbnailLink").as[String],
            (js \ "link").as[String],
            (js \ "image" \ "height").as[Int],
            (js \ "image" \ "width").as[Int]
          )
      }
      val imageList = new ListPage(pageNumber, images, totalResults)
      f(imageList)
    }

  }
}
