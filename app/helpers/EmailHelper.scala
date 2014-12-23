package helpers

import models.{CityInfo, Question, OrderInfo, ContactEntity}
import com.typesafe.plugin._
import play.api.Logger
import play.api.Play.current
import dao.common.{CityRepository, UserRepository}
import play.api.i18n.Messages
import play.api.mvc.{Request}

import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._

class EmailHelper(implicit userRepository: UserRepository) {

  //TODO: Refactor this, it should be injected with the constructor
  def cityRepository = db.Global.getControllerInstance(classOf[dao.common.CityRepository])

  def idMark(city: CityInfo, order: OrderInfo) = city.prefix.getOrElse("") + order.id.toString

  def systemEmail = userRepository.getSystemEmail

  def adminEmails = userRepository.getAdminEmails

  def createMailer = use[MailerPlugin].email

  def answerAvailability(subject: String, comment: String, email: String) = {
    val mail = use[MailerPlugin].email
    mail.addFrom(systemEmail)
    mail.addRecipient(email)
    mail.setSubject(subject)
    mail.sendHtml(comment)
  }

  def newQuestion(question: Question)(implicit request: Request[Any]) = {
    Akka.system.scheduler.scheduleOnce(1.second) {
      adminEmails.map {
        adminEmail =>
          val mail = use[MailerPlugin].email
          mail.setSubject("Запрос информации о наличии товара")
          mail.addFrom(systemEmail)
          mail.addRecipient(adminEmail)
          mail.sendHtml(views.html.emails.questions.availability(question).body)
      }
    }
    Akka.system.scheduler.scheduleOnce(1.second) {
      val mail = use[MailerPlugin].email
      mail.addFrom(systemEmail)
      mail.addRecipient(question.email)
      mail.setSubject("Запрос информации о наличии товара")
      mail.sendHtml(views.html.emails.questions.availabilityClient(question).body)
    }
  }

  def orderConfirmed(comment: String, order: OrderInfo)(implicit request: play.api.mvc.Request[Any]) = Akka.system.scheduler.scheduleOnce(1.second) {
    val mail = use[MailerPlugin].email
    val city = cityRepository.getByHostname(request.host)
    mail.setSubject(s"Подтверждение заказа ${idMark(city, order)}")
    mail.addFrom(systemEmail)
    mail.addRecipient(order.deliveryInfo.email)
    mail.sendHtml(comment)
  }

  def sendUnconfirmedOrdersExist(count: Int) = {
    adminEmails.map {
      email =>
        val mail = use[MailerPlugin].email
        mail.setSubject("Digitalband - eсть неподтвержденные заказы!")
        mail.addFrom(systemEmail)
        mail.addRecipient(email)
        mail.sendHtml(views.html.emails.order.unconfirmedExist(count).body)
    }
  }

  def sendUnansweredQuestionsExist(count: Int) = {
    adminEmails.map {
      email =>
        val mail = use[MailerPlugin].email
        mail.setSubject("Digitalband - eсть неотвеченные вопросы!")
        mail.addFrom(systemEmail)
        mail.addRecipient(email)
        mail.sendHtml(views.html.emails.questions.unanswered(count).body)
    }
  }

  def orderCanceled(comment: String, order: OrderInfo)(implicit request: Request[Any]) = Akka.system.scheduler.scheduleOnce(1.second) {
    val mail = use[MailerPlugin].email
    val city = cityRepository.getByHostname(request.host)
    mail.setSubject(s"Информация по заказу ${idMark(city, order)}")
    mail.addFrom(systemEmail)
    mail.addRecipient(order.deliveryInfo.email)
    mail.sendHtml(comment)
  }

  def sendFeedback(message: ContactEntity)(implicit request: Request[Any]) = Akka.system.scheduler.scheduleOnce(1.second) {
    adminEmails.map(email => send(message, email))
    def send(mess: ContactEntity, adminEmail: String) = {
      val mail: MailerAPI = use[MailerPlugin].email
      mail.setSubject(Messages("emailhelper.sendfeedback.subject"))
      mail.addRecipient(adminEmail)
      mail.addFrom(systemEmail)
      mail.setReplyTo(message.email)
      mail.sendHtml(views.html.emails.plain.contact.feedback(message).body)
    }
  }

  def sendPassword(email: String) = Akka.system.scheduler.scheduleOnce(1.second) {
    userRepository.getPassword(email) match {
      case Some(password) => {
        val mail = use[MailerPlugin].email
        mail.setSubject("Пароль к сайту Digitalband.ru")
        mail.addFrom(systemEmail)
        mail.addRecipient(email)
        mail.send(s"Ваш пароль: $password")
      }
      case None => {}
    }
  }

  def orderConfirmation(order: OrderInfo)(implicit request: Request[Any]) = Akka.system.scheduler.scheduleOnce(1.second) {
    val deliveryInfo = order.deliveryInfo
    val city = cityRepository.getByHostname(request.host)
    sendToClient(systemEmail, deliveryInfo.email)
    adminEmails.map(email => sendToAdmins(email, deliveryInfo.email, systemEmail))
    def sendToClient(from: String, to: String) = {
      val mail: MailerAPI = use[MailerPlugin].email
      val subject = getEmailSubject(order)
      Logger.info(s"Order notification to client: ${subject}")
      mail.setSubject(subject)
      mail.addRecipient(to)
      mail.addFrom(from)
      mail.sendHtml(views.html.emails.plain.order.notification(order).body)
    }
    def sendToAdmins(adminEmail: String, userEmail: String, systemEmail: String) = {
      val mail: MailerAPI = use[MailerPlugin].email
      val subject = getEmailSubject(order)
      Logger.info(s"Order notification to admins: ${subject}")
      mail.setSubject(subject)
      mail.addFrom(systemEmail)
      mail.setReplyTo(userEmail)
      mail.addRecipient(adminEmail)
      mail.sendHtml(views.html.emails.plain.order.adminNotification(order, idMark(city, order)).body)
    }
    def getEmailSubject(order: OrderInfo): String  = {
      val orderDetails =
        if(order.items.length > 1){
          val tailLength = order.items.tail.length
          val itemSuffix = tailLength % 10 match {
            case l if l > 1 && l < 5 => "а"
            case l if l > 5 => "ов"
          }
          order.items.head.title + Messages("emailhelper.orderconfirmation.subjectDetails", tailLength, itemSuffix)
        }
        else
          order.items.head.title
      Messages("emailhelper.orderconfirmation.subject", idMark(city, order), orderDetails)
    }
  }
}
