package helpers

import models.{OrderInfo, ContactEntity}
import com.typesafe.plugin._
import play.api.Play.current
import dao.common.UserRepository
import play.api.i18n.Messages

class EmailHelper(implicit userRepository: UserRepository) {
  def systemEmail = userRepository.getSystemEmail
  def adminEmails = userRepository.getAdminEmails

  def orderConfirmed(comment: String, order: OrderInfo)(implicit request: play.api.mvc.Request[Any]) = {
    val mail = use[MailerPlugin].email
    mail.setSubject(s"Подтверждение заказа №${order.id}")
    mail.addFrom(systemEmail)
    mail.addRecipient(order.deliveryInfo.email)
    mail.send(comment)
  }
  def orderCanceled(comment: String, order: OrderInfo)(implicit request: play.api.mvc.Request[Any]) = {
    val mail = use[MailerPlugin].email
    mail.setSubject(s"Информация по заказу №${order.id}")
    mail.addFrom(systemEmail)
    mail.addRecipient(order.deliveryInfo.email)
    mail.send(comment)
  }
  def sendFeedback(message: ContactEntity)(implicit request: play.api.mvc.Request[Any]) = {
    val mail: MailerAPI = use[MailerPlugin].email
    mail.setSubject(Messages("emailhelper.sendfeedback.subject"))
    val adminEmails = userRepository.getAdminEmails
    adminEmails.map(email => mail.addRecipient(email))
    mail.addFrom(systemEmail)
    mail.setReplyTo(message.email)
    mail.sendHtml(views.html.emails.plain.contact.feedback(message).body)
  }

  def orderConfirmation(order: OrderInfo)(implicit request: play.api.mvc.Request[Any]) = {
        val deliveryInfo = order.deliveryInfo
        sendToClient(systemEmail, deliveryInfo.email)
        sendToAdmins(adminEmails, deliveryInfo.email, systemEmail)
        def sendToClient(from: String, to: String) = {
          val mail: MailerAPI = use[MailerPlugin].email
          mail.setSubject(Messages("emailhelper.orderconfirmation.subject"))
          mail.addRecipient(to)
          mail.addFrom(from)
          mail.send(views.html.emails.plain.order.confirmation(order).body)
        }
        def sendToAdmins(adminEmails: Seq[String], userEmail: String, systemEmail: String) = {
          val mail: MailerAPI = use[MailerPlugin].email
          mail.setSubject(Messages("emailhelper.orderconfirmation.subject"))
          mail.addFrom(systemEmail)
          mail.setReplyTo(userEmail)
          adminEmails.map(email => mail.addRecipient(email))
          mail.send(views.html.emails.plain.order.adminConfirmation(order).body)
        }

  }
}
