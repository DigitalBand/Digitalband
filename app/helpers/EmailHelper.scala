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
    mail.addFrom(message.email)
    mail.sendHtml(views.html.emails.feedback(message).body)
  }

  def orderConfirmation(orderInfo: Option[OrderInfo])(implicit userRepository: UserRepository) = {
    orderInfo match {
      case Some(order) =>
        val deliveryInfo = order.deliveryInfo
        val systemEmail = userRepository.getSystemEmail
        sendToClient(systemEmail, deliveryInfo.email)
        sendToAdmins(userRepository.getAdminEmails, deliveryInfo.email, systemEmail)
        def sendToClient(from: String, to: String) = {
          val mail: MailerAPI = use[MailerPlugin].email
          mail.setSubject("Заказ на сайте Digitalband")
          mail.addRecipient(to)
          mail.addFrom(from)
          mail.sendHtml(views.html.emails.order.confirmation(orderInfo).body)
        }
        def sendToAdmins(adminEmails: Seq[String], userEmail: String, systemEmail: String) = {
          val mail: MailerAPI = use[MailerPlugin].email
          mail.setSubject("Заказ на сайте Digitalband")
          mail.addFrom(systemEmail)
          mail.setReplyTo(userEmail)
          adminEmails.map(email => mail.addRecipient(email))
          mail.sendHtml(views.html.emails.order.adminConfirmation(orderInfo).body)
        }
    }
  }
}
