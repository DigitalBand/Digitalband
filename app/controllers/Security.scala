package controllers

import com.google.inject.Inject
import common.ControllerBase
import play.api.mvc.{Action, Controller}
import dao.common.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import forms.loginForm


class Security @Inject()(val ur: UserRepository) extends ControllerBase(ur) {


  val forgotPasswordForm = Form("email" -> nonEmptyText.verifying(Messages("security.forgotpassword.notregistered"),
    result => result match {
      case email =>
        userRepository.get(email).isDefined
    }))

  //POST
  def signIn(login: String, password: String) = Action {
    implicit request =>
      loginForm(userRepository).bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.Security.login(formWithErrors)),
        user => {
          val (email, password) = user
          Redirect(routes.Application.index()).withSession("email" -> email)
        }
      )
  }

  //POST
  def signUp(login: String, password: String) = Action {
    NotImplemented
  }

  //GET
  def signOff = Action {
    Redirect(routes.Application.index()).withNewSession
  }

  //GET
  def login = Action {
    implicit request =>
      Ok(views.html.Security.login(forms.loginForm(userRepository)))
  }

  //GET
  def registration = Action {
    NotImplemented
  }

  //GET
  def forgotPassword = Action {
    implicit request =>
    Ok(views.html.Security.forgotpassword(forgotPasswordForm))
  }

  //POST
  def sendPassword = Action {
    NotImplemented
  }
}
