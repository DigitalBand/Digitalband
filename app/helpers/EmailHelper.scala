package helpers

import models.{CartItem, DeliveryInfo, OrderInfo, ContactEntity}
import com.typesafe.plugin._
import play.api.Play.current
import dao.common.{OrderRepository, UserRepository}

//TODO: This object contains localizable resources. Move to messages
object EmailHelper {
  def sendFeedback(message: ContactEntity)(implicit userRepository: UserRepository) = {
    val mail: MailerAPI = use[MailerPlugin].email
    mail.setSubject("Сообщение со страницы Контакты")
    val adminEmails = userRepository.getAdminEmails
    adminEmails.map(email => mail.addRecipient(email))
    mail.addFrom("tim@digitalband.ru")
    mail.setReplyTo(message.email)
    mail.send(views.html.emails.plain.contact.feedback(message).body)
  }

  def orderConfirmation(order: OrderInfo)(implicit userRepository: UserRepository) = {
        val deliveryInfo = order.deliveryInfo
        val systemEmail = userRepository.getSystemEmail
        sendToClient(systemEmail, deliveryInfo.email)
        sendToAdmins(userRepository.getAdminEmails, deliveryInfo.email, systemEmail)
        def sendToClient(from: String, to: String) = {
          val mail: MailerAPI = use[MailerPlugin].email

          mail.setSubject("Заказ на сайте Digitalband")
          mail.addRecipient(to)
          mail.addFrom(from)
          mail.send(views.html.emails.plain.order.confirmation(order).body)
        }
        def sendToAdmins(adminEmails: Seq[String], userEmail: String, systemEmail: String) = {
          val mail: MailerAPI = use[MailerPlugin].email
          mail.setSubject("Заказ на сайте Digitalband")
          mail.addFrom(systemEmail)
          mail.setReplyTo(userEmail)
          adminEmails.map(email => mail.addRecipient(email))
          mail.send(views.html.emails.plain.order.adminConfirmation(order).body)
        }

  }
}
