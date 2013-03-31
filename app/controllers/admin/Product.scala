package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{ImageRepository, ProductRepository, BrandRepository, UserRepository}
import helpers.{ImageHelper, Secured}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.{BrandEntity, ProductDetails}


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

  def deleteConfirmation(productId: Int) = withAdmin {
   implicit user =>
     implicit request =>
        val product = productRepository.get(productId)
        Ok(views.html.Admin.Product.deleteConfirmation(product))
  }

  def delete(productId: Int) = withAdmin {
    user => request =>
      productRepository.delete(productId) { image =>
        ImageHelper.deleteImage(image.path)
      }
      Redirect(controllers.routes.Product.list())
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
            val productId = formWithErrors("id").value.get.toInt
            val images = imageRepository.listByProductId(productId)
            BadRequest(views.html.Admin.Product.create(formWithErrors, images, productId))
          },
          product => {
            def addImages(pId: Int) = {
              request.body.asFormUrlEncoded.map {
                case (name, images) if name == "deletedImage" => {
                  images.map {
                    image => {
                      productRepository.removeImage(image.toInt, pId) {
                        imageId =>
                          val i = imageRepository.get(imageId)
                          imageRepository.remove(i.id)
                          ImageHelper.deleteImage(i.path)
                      }
                    }
                  }
                }
                case (name, images) if name == "googleimage" => {
                  images.map {
                    imageUrl => {
                      ImageHelper.save(imageUrl).map {
                        img =>
                          val imageId = imageRepository.create(img)
                          productRepository.insertImage(imageId, pId)
                          "success"
                      }.getOrElse(s"error: $imageUrl")
                    }
                  }
                }
                case _ => {}
              }
              request.body.files.map {
                file => {
                  ImageHelper.save(file) {
                    img =>
                      val imageId = imageRepository.create(img)
                      productRepository.insertImage(imageId, pId)
                  }
                }
              }
            }
            val productId = product.id match {
              case 0 => {
                productRepository.create(product, brandRepository.getBrandId, user.get.id) {
                  pId =>
                    addImages(pId)
                }
              }
              case _ => {
                productRepository.update(product, brandRepository.getBrandId, user.get.id) {
                  addImages(product.id)
                }
              }
            }
            Redirect(controllers.routes.Product.display(productId))
          }
        )
  }
}
