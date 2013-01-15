package controllers

import models._
import play.api._
import data.Form
import data.Forms._
import play.api.mvc._
import helpers.{ReCaptchaHelper, EmailHelper}
import com.google.inject.Inject
import dao.common.CategoryRepository

class Application @Inject()(val categoryRepository: CategoryRepository) extends Controller {

  def contactsForm(implicit request: Request[Any]) = {
    Form(
      mapping(
        "name" -> nonEmptyText,
        "email" -> nonEmptyText,
        "productName" -> nonEmptyText,
        "message" -> nonEmptyText,
        "recaptcha_challenge_field" -> nonEmptyText,
        "recaptcha_response_field" -> nonEmptyText
      )(Contact.apply)(Contact.unapply).verifying(contact => {
        ReCaptchaHelper.validate(
          ReCaptcha(
            contact.recaptcha_challenge_field,
            contact.recaptcha_response_field,
            request.remoteAddress,
            "6LfMQdYSAAAAAF1mfoJe--9UaVnA5BGjdQMlJ7sp"))
      }))
  }

  def index = Action {
    implicit request =>
      val categories: List[CategoryEntity] = categoryRepository.getListWithPictures()
      Ok(views.html.index(categories))
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
          Redirect(routes.Application.contacts())
        }
      )
  }
}