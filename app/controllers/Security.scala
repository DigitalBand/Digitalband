package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CartRepository, UserRepository}
import forms.{loginForm, registrationForm}
import helpers.{EmailHelper, withUser}
import play.api.Play.current
import play.api.cache.Cache
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._


class Security @Inject()(implicit ur: UserRepository, val cartRepository: CartRepository) extends ControllerBase {

  val forgotPasswordForm = Form(
    "email" -> nonEmptyText.verifying(
      Messages("security.forgotpassword.notregistered"),
      result => result match {
        case email =>
          Await.result(userRepository.get(email), Duration(2, SECONDS)).isDefined
      }
    )
  )
  val emailHelper = new EmailHelper()

  //POST
  def signIn(redirectUrl: String) = withUser.async {
    implicit user =>
      implicit request =>
        loginForm().bindFromRequest.fold(
          formWithErrors => Future(BadRequest(views.html.Security.login(formWithErrors, redirectUrl))),
          user => {
            val (email, _) = user
            for {
              userId <- userRepository.getUserId(email)
            } yield {
              cartRepository.mergeShoppingCarts(userId, getUserId)
              if (redirectUrl.isEmpty) {
                Redirect(routes.Application.index()).withSession {
                  "email" -> email
                }
              }
              else {
                Cache.remove(redirectUrl)
                Redirect(redirectUrl).withSession {
                  "email" -> email
                }
              }
            }
          }
        )
  }

  //POST
  def signUp(redirectUrl: String) = withUser.async {
    implicit user =>
      implicit request =>
        registrationForm().bindFromRequest.fold(
          formWithErrors => Future(BadRequest(views.html.Security.registration(formWithErrors, redirectUrl))),
          user => {
            val (email, password) = user
            for {
              userId <- userRepository.register(email, password)
            } yield {
              val anonymousUserId = getUserId
              cartRepository.mergeShoppingCarts(userId, anonymousUserId)
              userRepository.remove(anonymousUserId)
              if (redirectUrl.isEmpty) {
                Redirect(routes.Application.index()).withSession {
                  "email" -> email
                }
              }
              else {
                Cache.remove(redirectUrl)
                Redirect(redirectUrl).withSession {
                  "email" -> email
                }
              }
            }
          }
        )
  }

  //GET
  def signOff(redirectUrl: String) = withUser {
    implicit user =>
      implicit request =>
        if (redirectUrl.isEmpty)
          Redirect(routes.Application.index()).withNewSession
        else {
          Cache.remove(redirectUrl)
          Redirect(redirectUrl).withNewSession
        }
  }

  //GET
  def login(returnUrl: String) = withUser {
    implicit user =>
      implicit request =>
        Ok(views.html.Security.login(forms.loginForm(), returnUrl))
  }

  //GET
  def registration(redirectUrl: String) = withUser {
    implicit user =>
      implicit request =>
        Ok(views.html.Security.registration(registrationForm(), redirectUrl))
  }

  //GET
  def forgotPassword = withUser {
    implicit user =>
      implicit request =>
        Ok(views.html.Security.forgotpassword(forgotPasswordForm))
  }

  //POST
  def sendPassword = withUser {
    implicit user =>
      implicit request =>
        forgotPasswordForm.bindFromRequest.fold(
          withErrors => BadRequest(views.html.Security.forgotpassword(withErrors)),
          email => {
            emailHelper.sendPassword(email)
            Redirect(routes.Security.forgotPassword()).flashing(
              "alert" -> "Пароль был выслан на указанный email"
            )
          }
        )
  }
}
