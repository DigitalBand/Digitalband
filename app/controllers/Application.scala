package controllers


import common.ControllerBase
import models._
import play.api._
import data.Form
import data.Forms._
import play.api.mvc._
import play.api.Play.current
import helpers.{ReCaptchaHelper, EmailHelper}
import com.google.inject.Inject
import dao.common.{UserRepository, ProductRepository, CategoryRepository}

class Application @Inject()(implicit ur:UserRepository, val categoryRepository: CategoryRepository,
                            val productRepository: ProductRepository) extends ControllerBase  {
  val oneDayDuration = 86400

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
    Action {
      implicit request =>
        val categories: Seq[CategoryEntity] = categoryRepository.listWithPictures()
        val products: Seq[ProductEntity] = productRepository.listMostVisited(8)
        Ok(views.html.index(categories, products))
    }
 }

  def about = Action {
    implicit request =>
      Ok(views.html.Application.about())
  }

  def contacts = Action {
    implicit request =>
      Ok(views.html.Application.contacts(contactsForm))
  }

  def sendFeedback = Action {
    implicit request =>
      contactsForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.Application.contacts(formWithErrors)),
        contactsForm => {
          EmailHelper.sendFeedback(contactsForm)

          Redirect(routes.Application.contacts()).flashing(
            "alert-success" -> "Ваше сообщение было успешно отправлено"
          )
        }
      )
  }
}