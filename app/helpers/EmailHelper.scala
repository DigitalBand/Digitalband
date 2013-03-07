package helpers

import models.{OrderInfo, ContactEntity}
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

  def orderConfirmation(orderId: Int, email: String)(implicit userRepository: UserRepository, orderRepository: OrderRepository) = {
    val order = orderRepository.get(orderId)
    val systemEmail = userRepository.getSystemEmail
    sendToClient(systemEmail)
    sendToAdmins(userRepository.getAdminEmails, email, systemEmail)
    def sendToClient(systemEmail: String) = {
      val mail: MailerAPI = use[MailerPlugin].email
      mail.setSubject("Заказ на сайте Digitalband")
      mail.addRecipient(email)
      mail.addFrom(systemEmail)
      mail.sendHtml(views.html.emails.order.confirmation(order).body)
    }
    def sendToAdmins(adminEmails: Seq[String], userEmail: String, systemEmail: String) = {
      val mail: MailerAPI = use[MailerPlugin].email
      mail.setSubject("Заказ на сайте Digitalband")
      mail.addFrom(email)
      mail.setReplyTo(email)
      adminEmails.map(email => mail.addRecipient(email))
      mail.sendHtml(views.html.emails.order.adminConfirmation(order).body)
    }
  }
}
