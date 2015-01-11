package controllers

import controllers.common.ControllerBase
import dao.common.ProductRepository
import dao.impl.orm.slick.UserRepository
import com.google.inject.Inject
import helpers.withUser

class Rent @Inject()(implicit ur: UserRepository, productRepository: ProductRepository) extends ControllerBase{

  def requestRent(productId: Int) = withUser {
    implicit user =>
      implicit request =>
        val product = productRepository.get(productId)

        Ok(views.html.Rent.requestRent(product))
  }

  def postRequest(productId: Int) = withUser {
    implicit user =>
      implicit request =>

      Ok("")
  }
}
