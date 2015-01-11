package controllers

import controllers.common.ControllerBase
import dao.common.ProductRepository
import dao.impl.orm.slick.UserRepository
import com.google.inject.Inject
import helpers.withUser
import play.api.data.Form
import play.api.data.Forms._

case class RentRequest(
  quantity: Int,
  firstName: String,
  lastName: String,
  email: String,
  phone: String,
  city: String,
  street: String,
  notes: Option[String]
)

class Rent @Inject()(implicit ur: UserRepository, productRepository: ProductRepository) extends ControllerBase{

  val rentForm = Form(mapping(
      "quantity" -> number,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "phone" -> nonEmptyText,
      "city" -> nonEmptyText,
      "street" -> nonEmptyText,
      "notes" -> optional(text)
    )(RentRequest.apply)(RentRequest.unapply)
  )

  def requestRent(productId: Int) = withUser {
    implicit user =>
      implicit request =>
        val product = productRepository.get(productId)

        Ok(views.html.Rent.requestRent(product, rentForm))
  }

  def postRequest(productId: Int) = withUser {
    implicit user =>
      implicit request =>
      rentForm.bindFromRequest.fold(
        formWithErrors => {
          val product = productRepository.get(productId)
          BadRequest(views.html.Rent.requestRent(product, formWithErrors))
        },
        rentRequest => {
          val product = productRepository.get(productId)
          //RentEmailHelper.notifyNewRent(product, rentRequest)
          Ok("good")
        }
      )

  }
}
