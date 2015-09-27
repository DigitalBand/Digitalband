package controllers


import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common._
import helpers.{EmailHelper, ReCaptchaHelper, withUser}
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject()(implicit ur: UserRepository,
                            val shopRepository: ShopRepository,
                            val cityRepository: CityRepository,
                            val stockItemRepository: StockItemRepository,
                            val categoryRepository: CategoryRepository,
                            val productRepository: ProductRepository,
                            val pageRepository: PageRepository) extends ControllerBase {
  val oneDayDuration = 86400
  val emailHelper = new EmailHelper()

  def contactsForm(implicit request: Request[Any]) = {
    Form(
      mapping(
        "name" -> nonEmptyText,
        "email" -> nonEmptyText,
        "productName" -> nonEmptyText,
        "message" -> nonEmptyText,
        "recaptcha_challenge_field" -> nonEmptyText,
        "recaptcha_response_field" -> nonEmptyText
      )(ContactEntity.apply)(ContactEntity.unapply).verifying(contact => {
        ReCaptchaHelper.validate(
          ReCaptcha(
            contact.recaptcha_challenge_field,
            contact.recaptcha_response_field,
            request.remoteAddress,
            "6LfMQdYSAAAAAF1mfoJe--9UaVnA5BGjdQMlJ7sp"))
      }))
  }

  def index = withUser.async {
    implicit user =>
      implicit request =>
        for {
          categories <- categoryRepository.listWithPictures
          products <- productRepository.listMostVisited(12, request.host)
        } yield {
          Ok(views.html.index(categories, products))
        }
  }


  def pages(alias: String) = withUser.async {
    implicit user =>
      implicit request =>
        pageRepository.get(alias).map { page =>
          Ok(views.html.Application.page(page))
        }
  }

  def delivery = withUser.async {
    implicit user => implicit request =>
      cityRepository.getByHostname(request.host).map {
        cityInfo =>
          Ok(views.html.Application.delivery(cityInfo))
      }
  }

  def contacts = withUser {
    implicit user =>
      implicit request =>
        val recaptcha = ReCaptchaHelper.get("6LfMQdYSAAAAAJCe85Y6CRp9Ww7n-l3HOBf5bifB")
        val shops = shopRepository.getByHostname(request.host)
        Ok(views.html.Application.contacts(contactsForm, recaptcha, shops))
  }

  def stock(productId: Int) = withUser.async {
    implicit user =>
      implicit request =>
        for {
          product <- productRepository.get(productId)
        } yield {
          val shopList = stockItemRepository.shopList(productId)
          Ok(views.html.Application.stock(product, shopList))
        }
  }

  def sendFeedback = withUser {
    implicit user =>
      implicit request =>
        contactsForm.bindFromRequest.fold(
          formWithErrors => BadRequest(
            views.html.Application.contacts(formWithErrors,
              ReCaptchaHelper.get("6LfMQdYSAAAAAJCe85Y6CRp9Ww7n-l3HOBf5bifB"),
              shopRepository.getByHostname(request.host))),
          contactsForm => {
            emailHelper.sendFeedback(contactsForm)
            Redirect(routes.Application.contacts()).flashing(
              "alert-success" -> Messages("application.sendfeedback.alert.success")
            )
          }
        )
  }


}
