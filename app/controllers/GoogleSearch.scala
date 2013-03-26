package controllers

import controllers.common.ControllerBase
import com.google.inject.Inject
import dao.common.UserRepository
import helpers.Secured
import webServices.common.ImageSearch


class GoogleSearch @Inject()(implicit userRepository: UserRepository, imageSearch: ImageSearch) extends ControllerBase with Secured {

  def imageList(search: String, pageNumber: Int) = withAdmin {
    user => request => Async {
      imageSearch.getList(search, pageNumber) { imageList =>
        Ok(views.html.Admin.GoogleImages.list(imageList, search)).withHeaders(CACHE_CONTROL -> "public, max-age=86400")
      }
    }
  }

}
