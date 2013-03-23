package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.UserRepository
import helpers.Secured
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import models.ProductEntity


class Product @Inject()(implicit userRepository: UserRepository) extends ControllerBase with Secured {

 /* val productForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> nonEmptyText,
      "shortDescription" -> nonEmptyText,
      "imageId" -> number,
      "brandId" -> number,
    )(ProductEntity)
  )*/

  def create = withAdmin {
    user =>
      implicit request =>
        NotImplemented
  }

  def save = withAdmin {
    user =>
      implicit request =>
        NotImplemented
  }
}
