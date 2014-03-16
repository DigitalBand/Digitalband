package controllers


import common.ControllerBase
import models._
import play.api._
import data.Form
import data.Forms._
import i18n.Messages
import play.api.mvc._
import play.api.Play.current
import helpers.{Secured, ReCaptchaHelper, EmailHelper}
import com.google.inject.Inject
import dao.common._
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

class Application @Inject()(implicit ur: UserRepository,
                            val shopRepository: ShopRepository,
                            val categoryRepository: CategoryRepository,
                            val productRepository: ProductRepository) extends ControllerBase with Secured {
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
        implicit request => Future {
          val categories: Seq[CategoryEntity] = categoryRepository.listWithPictures
          val products: Seq[ProductDetails] = productRepository.listMostVisited(12)
          Ok(views.html.index(categories, products))
        }
    }
  }

  def about = withUser {
    implicit user =>
      implicit request => Future {
        Ok(views.html.Application.about())
      }
  }

  def contacts = withUser {
    implicit user =>
      implicit request => Future {
        val recaptcha = ReCaptchaHelper.get("6LfMQdYSAAAAAJCe85Y6CRp9Ww7n-l3HOBf5bifB")

        Ok(views.html.Application.contacts(contactsForm, recaptcha, shopRepository.list))
      }
  }

  def sendFeedback = withUser {
    implicit user =>
      implicit request => Future {
        contactsForm.bindFromRequest.fold(
          formWithErrors => BadRequest(
            views.html.Application.contacts(formWithErrors,
              ReCaptchaHelper.get("6LfMQdYSAAAAAJCe85Y6CRp9Ww7n-l3HOBf5bifB"),
              shopRepository.list)),
          contactsForm => {
            emailHelper.sendFeedback(contactsForm)
            Redirect(routes.Application.contacts()).flashing(
              "alert-success" -> Messages("application.sendfeedback.alert.success")
            )
          }
        )
      }
  }


}