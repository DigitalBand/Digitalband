package controllers.admin

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{ImageRepository, ProductRepository, BrandRepository, UserRepository}

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.{BrandEntity, ProductDetails}
import wt.common.image.ImageHelper
import helpers.withAdmin
import wt.common.DataStore
import play.api.{Routes, Play}
import java.nio.file.Paths
import play.api.Play.current
import com.codahale.jerkson.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Product @Inject()(implicit userRepository: UserRepository, brandRepository: BrandRepository, productRepository: ProductRepository, imageRepository: ImageRepository) extends ControllerBase {
  lazy val dataStore = new DataStore(Paths.get(System.getProperty("user.home"), Play.application.configuration.getString("data.root").get).toString)
  val productForm = Form(
    mapping(
      "id" -> optional(number),
      "title" -> nonEmptyText,
      "description" -> nonEmptyText,
      "shortDescription" -> nonEmptyText,
      "price" -> of[Double],
      "brand" -> nonEmptyText,
      "categoryId" -> number,
      "isAvailable" -> boolean
    )(ProductDetails.apply)(ProductDetails.unapply)
  )

  def javascriptRoutes = withAdmin {
    implicit user =>
      implicit request =>
        Ok(
          Routes.javascriptRouter("jsRoutes")(
            controllers.admin.routes.javascript.Product.listAllNotInStock,
            controllers.admin.routes.javascript.Product.deleteById
          )
        ).as("text/javascript")
  }

  def deleteNotInStockForm() = withAdmin {
    implicit user =>
      implicit request =>
        Ok(views.html.Admin.Product.deleteNotInStock())
  }
  def deleteById(id: Int) = withAdmin {
    implicit user =>
      implicit request =>
        productRepository.delete(id) {
          image =>
            ImageHelper(dataStore).deleteImage(image.path)
        }
        Ok("")
  }

  def listAllNotInStock = withAdmin {
    implicit user =>
      implicit request =>
        val ids = productRepository.getAllNotInStockIds
        Ok(Json.generate(ids))
  }

  def create(categoryId: Int, brandId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        brandRepository.get(brandId).map { b =>
          val brand = b match {
            case Some(b: BrandEntity) => b.title
            case _ => ""
          }
          Ok(views.html.Admin.Product.create(productForm.fill(new ProductDetails(categoryId, brand)), List(), 0))
        }
  }

  def deleteConfirmation(productId: Int) = withAdmin {
    implicit user =>
      implicit request =>
        val product = productRepository.get(productId)
        Ok(views.html.Admin.Product.deleteConfirmation(product))
  }

  def delete(productId: Int) = withAdmin {
    user => request =>
      productRepository.delete(productId) {
        image =>
          ImageHelper(dataStore).deleteImage(image.path)
      }
      Redirect(controllers.routes.Product.list())
  }

  def edit(productId: Int) = withAdmin.async {
    implicit user =>
      implicit request =>
        val product = productRepository.get0(productId)
        imageRepository.listByProductId(productId).map { images =>
          Ok(views.html.Admin.Product.create(productForm.fill(product), images, productId))
        }
  }

  def save = withAdmin.async(parse.multipartFormData) {
    implicit user =>
      implicit request =>
        productForm.bindFromRequest.fold(
          formWithErrors => {
            val productId = formWithErrors("id").value.get.toInt
            imageRepository.listByProductId(productId).map { images =>
              BadRequest(views.html.Admin.Product.create(formWithErrors, images, productId))
            }
          },
          product => {
            def addImages(pId: Int) = {
              request.body.asFormUrlEncoded.map {
                case (name, images) if name == "deletedImage" => {
                  images.foreach {
                    image => {
                      productRepository.removeImage(image.toInt, pId) {
                        imageId =>
                          imageRepository.get(imageId).map { i =>
                            imageRepository.remove(i.id)
                            ImageHelper(dataStore).deleteImage(i.path)
                          }
                      }
                    }
                  }
                }
                case (name, images) if name == "googleimage" => {
                  images.map {
                    imageUrl => {
                      ImageHelper(dataStore).save(imageUrl).map {
                        img =>
                          imageRepository.create(img).map { imageId =>
                            productRepository.insertImage(imageId, pId)
                            "success"
                          }
                      }.getOrElse(s"error: $imageUrl")
                    }
                  }
                }
                case _ =>
              }
              request.body.files.map {
                file => {
                  ImageHelper(dataStore).save(file.ref.file) {
                    img =>
                      imageRepository.create(img).map { imageId =>
                        productRepository.insertImage(imageId, pId)
                      }
                      img

                  }
                }
              }
            }
            for {
              brandId <- brandRepository.getBrandId(product.brand.title)
            } yield {
              val productId = product.id match {
                case 0 => {
                  productRepository.create(product, brandId, user.get.id) {
                    pId =>
                      addImages(pId)
                  }
                }
                case _ => {
                  productRepository.update(product, brandId, user.get.id) {
                    addImages(product.id)
                  }
                }
              }
              Redirect(controllers.routes.Product.display(productId))
            }

          }
        )
  }
}
