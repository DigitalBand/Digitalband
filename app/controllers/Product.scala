package controllers

import common.ControllerBase
import play.api.mvc.{Request, AnyContent, Action}
import com.google.inject.Inject
import models.UserEntity
import dao.common._
import play.api._
import data.Form
import data.Forms._
import play.api.Play.current
import play.api.cache.Cached
import helpers.EmailHelper


class Product @Inject()(implicit ur: UserRepository, productRepository: ProductRepository,
                        categoryRepository: CategoryRepository,
                        imageRepository: ImageRepository,
                        brandRepository: BrandRepository) extends ControllerBase {
  val availabilityForm = Form("email" -> nonEmptyText)

  def list = filteredList(1)

  def availability(id: Int, returnUrl: String) = withUser {
    implicit user =>
      implicit request =>
        val product = productRepository.get(id)
        Ok(views.html.Product.availability(product, availabilityForm, isAjax, returnUrl))
  }

  def requestAvailability(id: Int, returnUrl: String) = withUser {
    implicit user => implicit request =>
      val product = productRepository.get(id)
      availabilityForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.Product.availability(product, formWithErrors, isAjax, returnUrl))
        },
        email => {
          if (productRepository.requestAvailability(product.id, email)) {
            val emailHelper = new EmailHelper()
            emailHelper.newQuestion(product, email, "availability")
            if (!isAjax) {
              redirectToReturnUrlOrProduct(returnUrl, product.id).flashing("alert-success" -> "Запрос успешно отправлен")
            } else {
              Ok("success")
            }
          } else {
            redirectToReturnUrlOrProduct(returnUrl, product.id)
              .flashing("alert-warning" -> "Вы уже отправили запрос по этому товару. Ожидайте ответа")
          }
        }
      )
  }

  def redirectToReturnUrlOrProduct(returnUrl: String, productId: Int) = {
    if (returnUrl.isEmpty)
      Redirect(routes.Product.display(productId))
    else
      Redirect(returnUrl)
  }

  def filteredList(categoryId: Int,
                   pageNumber: Int = 1,
                   brandId: Int = 0,
                   brandPage: Int = 1,
                   productId: Int = 0,
                   pageSize: Int = 10,
                   search: String = "") =
  //Cached(req => req.uri, 82000) {
    withUser {
      implicit user =>
        implicit request =>
          if (productId > 0)
            display(productId, categoryId, brandId, brandPage, pageNumber, search)
          else {
            val products = productRepository.getList(categoryRepository.get(categoryId), brandId, pageNumber, pageSize, search)
            if (categoryId == 1 && !search.isEmpty && products.totalCount == 1)
              Redirect(routes.Product.display(products.items.head.id))
            else
              Ok(views.html.Product.list(
                products,
                categoryRepository.get(categoryId),
                categoryRepository.list(categoryId, brandId, search),
                brandRepository.list(categoryRepository.get(categoryId), brandPage, pageSize = 24, search),
                brandRepository.get(brandId),
                pageNumber,
                pageSize,
                categoryRepository.getBreadcrumbs(categoryId, productId, search),
                search))
          }
    }

  //}

  def display(id: Int): Action[AnyContent] =
  //Cached(req => req.toString, 82000) {
    withUser {
      implicit user =>
        implicit request =>
          display(id, 1, 0, 1, 1, "")
    }

  //}

  //TODO: Make async
  def display(id: Int, categoryId: Int, brandId: Int, brandPage: Int, pageNumber: Int, search: String)(implicit request: Request[AnyContent], user: Option[UserEntity]) = {
    Ok(views.html.Product.display(
      productRepository.get(id, brandRepository.get),
      imageRepository.listByProductId(id),
      categoryRepository.list(categoryId, brandId, search),
      brandRepository.list(categoryRepository.get(categoryId), brandPage, pageSize = 24, search),
      categoryId, brandId,
      categoryRepository.getBreadcrumbs(categoryId, id, ""), pageNumber, search))
  }
}

