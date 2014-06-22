package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common._
import helpers.{EmailHelper, withUser}
import models.UserEntity
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._


class Product @Inject()(implicit ur: UserRepository, productRepository: ProductRepository,
                        categoryRepository: CategoryRepository,
                        imageRepository: ImageRepository,
                        brandRepository: BrandRepository,
                        questionRepository: QuestionRepository) extends ControllerBase {
  val emailHelper = new EmailHelper()
  def availabilityForm = Form("email" -> nonEmptyText)

  def list = filteredList(1)

  def availability(id: Int, returnUrl: String) = withUser {
    implicit user =>
      implicit request =>
        val product = productRepository.get(id)
        Ok(views.html.Product.availability(product, availabilityForm, isAjax, returnUrl))
  }

  def requestAvailability(id: Int, returnUrl: String) = withUser {
    implicit user =>
      implicit request =>
        val product = productRepository.get(id)
        availabilityForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(views.html.Product.availability(product, formWithErrors, isAjax, returnUrl))
          },
          email => {
            questionRepository.insertQuestion(product.id, email).map {
              questionId =>
                val question = questionRepository.get(questionId)

                emailHelper.newQuestion(question)
                if (!isAjax) {
                  redirectToReturnUrlOrProduct(returnUrl, product.id).flashing("alert-success" -> "Запрос успешно отправлен")
                } else {
                  Ok("success")
                }
            }.getOrElse {
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
                   search: String = "",
                   isAvailable: Int = 0) =
  //Cached(req => req.uri, 82000) {
    withUser {
      implicit user =>
        implicit request =>
          val inStock = isAvailable > 0
          if (productId > 0)
            display(productId, categoryId, brandId, brandPage, pageNumber, search, isAvailable)
          else {
            val products = productRepository.getList(categoryRepository.get(categoryId), brandId, pageNumber, pageSize, search, inStock)
            if (categoryId == 1 && !search.isEmpty && products.totalCount == 1)
              Redirect(routes.Product.display(products.items.head.id))
            else {
              val category = categoryRepository.get(categoryId)
              val brand = brandRepository.get(brandId)
              val helper = helpers.PagerHelper(pageSize, products.totalCount, products.number)
              def url(pn: Int) = routes.Product.filteredList(category.id, pn, helpers.ViewHelper.brandId(brand), s = search, is = isAvailable)
              def isInStock(inS: Int) = routes.Product.filteredList(category.id, pageNumber, helpers.ViewHelper.brandId(brand), s = search, is = inS)
              Ok(views.html.Product.list(
                products,
                category,
                categoryRepository.list(categoryId, brandId, search, inStock),
                brandRepository.list(categoryRepository.get(categoryId), brandPage, pageSize = 24, search, inStock),
                brand,
                pageNumber,
                pageSize,
                categoryRepository.getBreadcrumbs(categoryId, productId, search),
                search, helper, url, isInStock, inStock = isAvailable))
            }
          }
    }

  //}
  def isAdmin(userO: Option[UserEntity]) = userO match {
    case Some(user) => user.isAdmin;
    case None => false
  }

  def display(id: Int): EssentialAction =
  //Cached(req => req.toString, 82000) {
    withUser {
      implicit user =>
        implicit request =>
          display(id, 1, 0, 1, 1, "")
    }

  //}

  //TODO: Make async
  def display(id: Int, categoryId: Int, brandId: Int, brandPage: Int, pageNumber: Int, search: String, inStock: Int = 0)(implicit request: Request[AnyContent], user: Option[UserEntity]) = {
    val product = productRepository.get(id, brandRepository.get)

    Ok(views.html.Product.display(
      product,
      imageRepository.listByProductId(id),
      categoryRepository.list(categoryId, brandId, search, inStock == 1),
      brandRepository.list(categoryRepository.get(categoryId), brandPage, pageSize = 24, search, inStock == 1),
      categoryId, brandId,
      categoryRepository.getBreadcrumbs(categoryId, id, ""), pageNumber, search, isAdmin(user), inStock))
  }
}

