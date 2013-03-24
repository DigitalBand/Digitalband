package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{ImageRepository, ProductRepository, BrandRepository, UserRepository}
import helpers.{ImageHelper, Secured}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.i18n.Messages
import models.{BrandEntity, ProductDetails}
import play.api.mvc.Action


class Product @Inject()(implicit userRepository: UserRepository, brandRepository: BrandRepository, productRepository: ProductRepository, imageRepository: ImageRepository) extends ControllerBase with Secured {

  val productForm = Form(
    mapping(
      "id" -> optional(number),
      "title" -> nonEmptyText,
      "description" -> nonEmptyText,
      "shortDescription" -> nonEmptyText,
      "price" -> of[Double],
      "brand" -> nonEmptyText,
      "categoryId" -> number
    )(ProductDetails.apply)(ProductDetails.unapply)
  )

  def create(categoryId: Int, brandId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        val brand = brandRepository.get(brandId) match {
          case Some(b: BrandEntity) => b.title
          case _ => ""
        }
        Ok(views.html.Admin.Product.create(productForm.fill(new ProductDetails(categoryId, brand)), List(), 0))
  }

  def edit(productId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        val product = productRepository.get(productId, brandRepository.get)
        val images = imageRepository.listByProductId(productId)
        Ok(views.html.Admin.Product.create(productForm.fill(product), images, productId))
  }

  def save = withAdmin(parse.multipartFormData) {
    implicit user =>
      implicit request =>
        productForm.bindFromRequest.fold(
          formWithErrors => {
            val productId = Int.unbox(formWithErrors("id").value)
            val images = imageRepository.listByProductId(productId)
            BadRequest(views.html.Admin.Product.create(formWithErrors, images, productId))
          },
          product => {
            val file = request.body.file("image")
            val imageId = ImageHelper.save(file)(img => imageRepository.create(img))
            val id = productRepository.create(product, imageId, brandRepository.getBrandId, user.get.id)
            Redirect(controllers.routes.Product.display(id))
          }
        )
  }
}
