package controllers.admin

import controllers.common.ControllerBase
import com.google.inject.Inject
import dao.common.UserRepository
import helpers.withAdmin


class ImageSearch @Inject()(implicit userRepository: UserRepository, imageSearch: webServices.common.ImageSearch) extends ControllerBase {

  def imageList(search: String, pageNumber: Int) = withAdmin.async {
    user => request =>
        imageSearch.getList(search, pageNumber) {
          imageList =>
            Ok(views.html.Admin.GoogleImages.list(imageList, search)).withHeaders(CACHE_CONTROL -> "public, max-age=86400")
        }

  }

}
