package controllers

import com.google.inject.Inject
import common.ControllerBase
import play.api.mvc.Action
import dao.common.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import forms.loginForm
import play.api.cache.Cache
import play.api.Play.current


class Security @Inject()(val ur: UserRepository) extends ControllerBase(ur) {


  val forgotPasswordForm = Form("email" -> nonEmptyText.verifying(Messages("security.forgotpassword.notregistered"),
    result => result match {
      case email =>
        userRepository.get(email).isDefined
    }))

  //POST
  def signIn(redirectUrl: String) = Action {
    implicit request =>
      loginForm(userRepository).bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.Security.login(formWithErrors, redirectUrl)),
        user => {
          val (email, password) = user
          if (redirectUrl.isEmpty)
            Redirect(routes.Application.index()).withSession{
              "email" -> email
            }
          else {
            Cache.remove(redirectUrl)
            Redirect(redirectUrl).withSession{
              "email" -> email
            }
          }
        }
      )
  }

  //POST
  def signUp(redirectUrl: String) = Action {
    NotImplemented
  }

  //GET
  def signOff(redirectUrl: String) = Action {
    implicit request =>
    if (redirectUrl.isEmpty)
      Redirect(routes.Application.index()).withNewSession
    else {
      Cache.remove(redirectUrl)
      Redirect(redirectUrl).withNewSession
    }
  }

  //GET
  def login(returnUrl: String) = Action {
    implicit request =>
      Ok(views.html.Security.login(forms.loginForm(userRepository), returnUrl))
  }

  //GET
  def registration(redirectUrl: String) = Action {
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
