package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common._
import forms.ContactsForm
import helpers.{EmailHelper, ReCaptchaHelper, withUser}
import models._
import play.api.data.Form
import play.api.i18n.Messages
import scala.concurrent.Future
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

  def contacts = withUser.async {
    implicit user =>
      implicit request =>
        for {
        //TODO: Save the string to the config
          recaptcha <- ReCaptchaHelper.get("6LfMQdYSAAAAAJCe85Y6CRp9Ww7n-l3HOBf5bifB")
          shops <- shopRepository.getByHostname(request.host)
        } yield Ok(views.html.Application.contacts(ContactsForm(), recaptcha, shops))
  }

  def stock(productId: Int) = withUser.async {
    implicit user =>
      implicit request =>
        for {
          product <- productRepository.get(productId)
          shopList <- stockItemRepository.shopList(productId)
        } yield {
          Ok(views.html.Application.stock(product, shopList))
        }
  }

  def sendFeedback = withUser.async {
    implicit user =>
      implicit request =>
        def onError(form: Form[ContactEntity]) = {
          for {
            //TODO: Save the string to the config
            recaptcha <- ReCaptchaHelper.get("6LfMQdYSAAAAAJCe85Y6CRp9Ww7n-l3HOBf5bifB")
            shops <- shopRepository.getByHostname(request.host)
          } yield BadRequest(
            views.html.Application.contacts(form, recaptcha, shops))
        }
        def onSuccess(contactEntity: ContactEntity) = {
          emailHelper.sendFeedback(contactEntity)
          Future {
            Redirect(routes.Application.contacts()).flashing(
              "alert-success" -> Messages("application.sendfeedback.alert.success")
            )
          }
        }
        ContactsForm().bindFromRequest.fold(onError, onSuccess)
  }


}
