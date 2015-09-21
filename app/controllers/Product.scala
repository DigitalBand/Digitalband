package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common._
import helpers.{EmailHelper, withUser}
import models.{BrandEntity, UserEntity}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

class Product @Inject()(implicit ur: UserRepository, productRepository: ProductRepository,
                        categoryRepository: CategoryRepository,
                        imageRepository: ImageRepository,
                        brandRepository: BrandRepository,
                        questionRepository: QuestionRepository) extends ControllerBase {
  val emailHelper = new EmailHelper()

  def availabilityForm = Form("email" -> nonEmptyText)

  def host(implicit request: Request[AnyContent]) = request.host

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
                   isAvailable: Int = 0) = withUser.async {
    implicit user =>
      implicit request =>
        val inStock = isAvailable > 0
        if (productId > 0)
          display(productId, categoryId, brandId, brandPage, pageNumber, search, isAvailable)
        else {
          for {
            brand <- brandRepository.get(brandId)
            category <- categoryRepository.get(categoryId)
            categories <- categoryRepository.list(categoryId, brandId, search, inStock, host)
            brands <- brandRepository.list(category, brandPage, pageSize = 24, search, inStock, host)
            breadcrumbs <- categoryRepository.getBreadcrumbs(categoryId, productId, search)
          } yield {
            val products = productRepository.getList(category, brandId, pageNumber, pageSize, search, inStock, host)
            if (categoryId == 1 && !search.isEmpty && products.totalCount == 1)
              Redirect(routes.Product.display(products.items.head.id))
            else {
              val helper = helpers.PagerHelper(pageSize, products.totalCount, products.number)
              def url(pn: Int) = routes.Product.filteredList(category.id, pn, brandId, s = search, is = isAvailable)
              def isInStock(inS: Int, brand: Option[BrandEntity]) =
                routes.Product.filteredList(category.id, pageNumber, helpers.ViewHelper.brandId(brand), s = search, is = inS)

              Ok(views.html.Product.list(
                products,
                category,
                categories,
                brands,
                brand,
                pageNumber,
                pageSize,
                breadcrumbs,
                search,
                helper,
                url,
                isInStock,
                inStock = isAvailable))
            }
          }
        }
  }

  def isAdmin(userO: Option[UserEntity]) = userO match {
    case Some(user) => user.isAdmin;
    case None => false
  }

  def display(id: Int): EssentialAction = withUser.async {
    implicit user =>
      implicit request =>
        display(id, 1, 0, 1, 1, "")
  }


  def display(id: Int, categoryId: Int, brandId: Int, brandPage: Int, pageNumber: Int, search: String, inStock: Int = 0)
             (implicit request: Request[AnyContent], user: Option[UserEntity]) = {

    val product = productRepository.get(id)

    for {
      category <- categoryRepository.get(categoryId)
      categories <- categoryRepository.list(categoryId, brandId, search, inStock == 1, host)
      breadcrumbs <- categoryRepository.getBreadcrumbs(categoryId, id, "")
      imageIds <- imageRepository.listByProductId(id)
      brands <- brandRepository.list(category, brandPage, pageSize = 24, search, inStock == 1, host)
    } yield Ok(views.html.Product.display(
      product,
      imageIds,
      categories,
      brands,
      categoryId, brandId,
      breadcrumbs, pageNumber, search, isAdmin(user), inStock)
    )
  }
}

