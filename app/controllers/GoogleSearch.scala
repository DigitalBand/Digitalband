package controllers

import controllers.common.ControllerBase
import com.google.inject.Inject
import dao.common.UserRepository
import helpers.Secured
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import models.{ListPage, GoogleImage}
import play.api.libs.json.JsArray


class GoogleSearch @Inject()(implicit userRepository: UserRepository) extends ControllerBase with Secured {

  def imageList(search: String, pageNumber: Int) = withAdmin {
    user => request => Async {
      val num = 10
      val offset = (pageNumber - 1) * num + 1
      val key = "AIzaSyAHyhiLNqUOfhcf362AoEEPl6s65UhzbMg"//"AIzaSyDByv-9nh9XS9RpLSMUgqj7z6iUejzG8nY"
      val cx = "009346898408990037447:evnb2u_eytw"//"006243113112137199630:y3v5ecyacm8"
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
            new GoogleImage(
              (js \ "image" \ "thumbnailLink").as[String],
              (js \ "link").as[String],
              (js \ "image" \ "height").as[Int],
              (js \ "image" \ "width").as[Int]
            )
        }
        val imageList = new ListPage(pageNumber, images, totalResults)
        Ok(views.html.Admin.GoogleImages.list(imageList, search)).withHeaders(CACHE_CONTROL -> "public, max-age=86400")
      }
    }
  }

}
