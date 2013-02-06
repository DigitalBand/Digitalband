package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import dao.common.UserRepository
import play.api.data.Form
import play.api.data.Forms._

class Security @Inject()(val userRepository: UserRepository) extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    ) verifying("Invalid email or password", result =>
      result match {
        case (email, password) =>
          userRepository.authenticate(email, password).isDefined
      })
  )
  //POST
  def signIn(login:String, password:String) = Action {
    NotImplemented
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
}
