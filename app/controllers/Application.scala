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

  def index = /*Cached("homePage", oneDayDuration)*/ {
    withUser {
      implicit user =>
        implicit request =>
          val categories: Seq[CategoryEntity] = categoryRepository.listWithPictures
          val products: Seq[ProductDetails] = productRepository.listMostVisited(12, request.host)
          Ok(views.html.index(categories, products))
    }
  }

  def pages(alias: String) = withUser {
    implicit user =>
      implicit request =>
        val page = pageRepository.get(alias)
        Ok(views.html.Application.page(page))
  }

  def delivery = withUser {
    implicit user => implicit request =>
      Ok(views.html.Application.delivery(cityRepository.getByHostname(request.host)))
  }

  def contacts = withUser {
    implicit user =>
      implicit request =>
        val recaptcha = ReCaptchaHelper.get("6LfMQdYSAAAAAJCe85Y6CRp9Ww7n-l3HOBf5bifB")
        val shops = shopRepository.getByHostname(request.host)
        Ok(views.html.Application.contacts(contactsForm, recaptcha, shops))
  }

  def stock(productId: Int) = withUser {
    implicit user =>
      implicit request =>
        val product = productRepository.get(productId)
        val shopList = stockItemRepository.shopList(productId)
        Ok(views.html.Application.stock(product, shopList))
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
