package forms

import helpers.ReCaptchaHelper
import models.{ReCaptcha, ContactEntity}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Request

object ContactsForm {
  def apply()(implicit request: Request[Any]): Form[ContactEntity] = Form(
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
