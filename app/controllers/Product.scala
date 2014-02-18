package controllers

import common.ControllerBase
import play.api.mvc._
import com.google.inject.Inject
import models.UserEntity
import dao.common._
import play.api._
import data.Form
import data.Forms._
import play.api.Play.current
import play.api.cache.Cached
import helpers.{Secured, EmailHelper}


class Product @Inject()(implicit ur: UserRepository, productRepository: ProductRepository,
                        categoryRepository: CategoryRepository,
                        imageRepository: ImageRepository,
                        brandRepository: BrandRepository,
                        questionRepository: QuestionRepository) extends ControllerBase with Secured {
  val emailHelper = new EmailHelper()
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
          questionRepository.insertQuestion(product.id, email).map { questionId =>
            val question = questionRepository.get(questionId)

            emailHelper.newQuestion(question)
            if (!isAjax) {
              redirectToReturnUrlOrProduct(returnUrl, product.id).flashing("alert-success" -> "Запрос успешно отправлен")
            } else {
              Ok("success")
            }
          }.getOrElse{
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
          val inStock = request.queryString.get("inStock").flatMap(seq => Some(seq.head == "true")) getOrElse false

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
  def isAdmin(userO: Option[UserEntity]) = userO match {case Some(user) => user.isAdmin; case None => false}
  def display(id: Int): EssentialAction =
  //Cached(req => req.toString, 82000) {
    withUser {
      implicit user =>
        implicit request =>
          display(id, 1, 0, 1, 1, "")
    }

  //}

  //TODO: Make async
  def display(id: Int, categoryId: Int, brandId: Int, brandPage: Int, pageNumber: Int, search: String)(implicit request: Request[AnyContent], user: Option[UserEntity]) = {
    val product = productRepository.get(id, brandRepository.get).copy(isAvailable = productRepository.getAvailability(id) > 0)
    Ok(views.html.Product.display(
      product,
      imageRepository.listByProductId(id),
      categoryRepository.list(categoryId, brandId, search),
      brandRepository.list(categoryRepository.get(categoryId), brandPage, pageSize = 24, search),
      categoryId, brandId,
      categoryRepository.getBreadcrumbs(categoryId, id, ""), pageNumber, search, isAdmin(user)))
  }
}

