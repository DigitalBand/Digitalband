package controllers

import controllers.common.ControllerBase
import dao.common.{BrandRepository, ProductRepository}
import dao.impl.orm.slick.UserRepository
import com.google.inject.Inject
import helpers.{EmailHelper, withUser}
import play.api.data.Form
import play.api.data.Forms._
import scala.concurrent.ExecutionContext.Implicits.global

case class RentRequest(
                        quantity: Int = 1,
                        firstName: String = "",
                        lastName: String = "",
                        email: String = "",
                        phone: String = "",
                        city: String = "",
                        street: String = "",
                        notes: Option[String] = Some("")
                        )

class Rent @Inject()(implicit ur: UserRepository, productRepository: ProductRepository, brandRepository: BrandRepository) extends ControllerBase {

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

  def requestRent(productId: Int) = withUser.async {
    implicit user =>
      implicit request =>
        for {
          product <- productRepository.get0(productId)
        } yield {
          rentForm.fill(RentRequest(quantity = 1))
          Ok(views.html.Rent.requestRent(product, rentForm.fill(RentRequest(quantity = 1))))
        }
  }

  def postRequest(productId: Int) = withUser.async {
    implicit user =>
      implicit request =>
        for {
          product <- productRepository.get0(productId)
        } yield rentForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(views.html.Rent.requestRent(product, formWithErrors))
          },
          rentRequest => {
            val emailHelper = new EmailHelper()
            emailHelper.notifyAboutNewRent(product, rentRequest)
            Ok(views.html.Rent.success(product, rentRequest))
          }
        )

  }
}
