package helpers

import models.Contact
import com.typesafe.plugin._
import play.api.Play.current

object EmailHelper {
  def sendFeedback(message: Contact) = {
    val mail: MailerAPI = use[MailerPlugin].email
    mail.setSubject("Сообщение со страницы Контакты")
    mail.addRecipient("tim@digitalband.ru")
    mail.addFrom(message.email)
    mail.sendHtml(views.html.emails.feedback(message).body)
  }
}
