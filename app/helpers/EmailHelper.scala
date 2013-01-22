package helpers

import models.ContactEntity
import com.typesafe.plugin._
import play.api.Play.current

object EmailHelper {
  def sendFeedback(message: ContactEntity) = {
    val mail: MailerAPI = use[MailerPlugin].email
    mail.setSubject("Сообщение со страницы Контакты")
    mail.addRecipient("tim@digitalband.ru")
    mail.addFrom(message.email)
    mail.sendHtml(views.html.emails.feedback(message).body)
  }
}
