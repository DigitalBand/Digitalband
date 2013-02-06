package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import dao.common.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import helpers.EmailHelper

class Security @Inject()(val userRepository: UserRepository) extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    ) verifying(Messages("validation.login.wronginfo"), result =>
      result match {
        case (email, password) =>
          userRepository.authenticate(email, password).isDefined
      })
  )
  val forgotPasswordForm = Form("email" -> nonEmptyText.verifying(Messages("security.forgotpassword.notregistered"),
  result => result match {
    case email =>
      userRepository.get(email).isDefined
  }))
  //POST
  def signIn(login:String, password:String) = Action {
    implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.Security.login(formWithErrors)),
      contactsForm => {
        Redirect(routes.Application.index())
      }
    )
  }

  //POST
  def signUp(login:String, password:String) = Action {
    NotImplemented
  }

  //GET
  def signOff = Action {
    NotImplemented
  }
  //GET
  def login = Action {
    implicit request =>
      Ok(views.html.Security.login(loginForm))
  }
  //GET
  def registration = Action {
    NotImplemented
  }
  //GET
  def forgotPassword = Action { Ok(views.html.Security.forgotpassword(forgotPasswordForm)) }
  //POST
  def sendPassword = Action { NotImplemented }
}
