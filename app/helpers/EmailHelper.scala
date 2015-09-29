package helpers

import play.api.libs.mailer._
import controllers.RentRequest
import dao.common.UserRepository
import models._
import play.api.Logger
import play.api.Play.current
import play.api.i18n.Messages
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Request

import scala.concurrent.duration._

class EmailHelper(implicit userRepository: UserRepository) {

  //TODO: Refactor this, it should be injected with the constructor
  def cityRepository = db.Global.getControllerInstance(classOf[dao.common.CityRepository])

  def idMark(city: CityInfo, order: OrderInfo) = city.prefix.getOrElse("") + order.id.toString

  def systemEmail = userRepository.getSystemEmail

  def adminEmails = userRepository.getAdminEmails

  def notifyAboutNewRent(details: ProductDetails, request: RentRequest) = {
    val mail = Email(
      subject = s"Заказ аренды ${details.title}",
      from = systemEmail,
      to = Seq(systemEmail),
      bodyHtml = Some(views.html.emails.rent.newRentNotification(details, request).body)
    )
    MailerPlugin.send(mail)
  }

  def answerAvailability(subject: String, comment: String, email: String) = {
    val mail = Email(
      subject = subject,
      from = systemEmail,
      to = Seq(email),
      bodyHtml = Some(comment)
    )
    MailerPlugin.send(mail)
  }

  def newQuestion(question: Question)(implicit request: Request[Any]) = {
    Akka.system.scheduler.scheduleOnce(1.second) {
      adminEmails.map {
        adminEmail =>
          val mail = Email(
            subject = "Запрос информации о наличии товара",
            from = systemEmail,
            to = Seq(adminEmail),
            bodyHtml = Some(views.html.emails.questions.availability(question).body)
          )
          MailerPlugin.send(mail)
      }
    }
    Akka.system.scheduler.scheduleOnce(1.second) {
      val mail = Email(
        from = systemEmail,
        to = Seq(question.email),
        subject = "Запрос информации о наличии товара",
        bodyHtml = Some(views.html.emails.questions.availabilityClient(question).body)
      )
      MailerPlugin.send(mail)
    }
  }

  def orderConfirmed(comment: String, order: OrderInfo)(implicit request: play.api.mvc.Request[Any]) = Akka.system.scheduler.scheduleOnce(1.second) {
    cityRepository.getByHostname(request.host).map { city =>
      val mail = Email(
        subject = s"Подтверждение заказа ${idMark(city, order)}",
        from = systemEmail,
        to = Seq(order.deliveryInfo.email),
        bodyHtml = Some(comment)
      )
      MailerPlugin.send(mail)
    }
  }

  def sendUnconfirmedOrdersExist(orders: Map[Option[String], Int]) = {
    adminEmails.map {
      email =>
        val mail = Email(
          subject = "Digitalband - eсть неподтвержденные заказы!",
          from = systemEmail,
          to = Seq(email),
          bodyHtml = Some(views.html.emails.order.unconfirmedExist(orders).body)
        )
        MailerPlugin.send(mail)
    }
  }

  def orderCanceled(comment: String, order: OrderInfo)(implicit request: Request[Any]) = Akka.system.scheduler.scheduleOnce(1.second) {
    cityRepository.getByHostname(request.host).map { city =>
      val mail = Email(
        subject = s"Информация по заказу ${idMark(city, order)}",
        from = systemEmail,
        to = Seq(order.deliveryInfo.email),
        bodyHtml = Some(comment)
      )
      MailerPlugin.send(mail)
    }
  }

  def sendFeedback(message: ContactEntity)(implicit request: Request[Any]) = Akka.system.scheduler.scheduleOnce(1.second) {
    adminEmails.map(email => send(message, email))
    def send(mess: ContactEntity, adminEmail: String) = {
      val mail = Email(
        subject = Messages("emailhelper.sendfeedback.subject"),
        to = Seq(adminEmail),
        from = systemEmail,
        replyTo = Some(message.email),
        bodyHtml = Some(views.html.emails.plain.contact.feedback(message).body)
      )
      MailerPlugin.send(mail)
    }
  }

  def sendPassword(email: String) = Akka.system.scheduler.scheduleOnce(1.second) {
    for {
      passwordOption <- userRepository.getPassword(email)
    } yield {
      passwordOption match {
        case Some(password) =>
          val mail = Email(
            subject = "Пароль к сайту Digitalband.ru",
            from = systemEmail,
            to = Seq(email),
            bodyText = Some(s"Ваш пароль: $password")
          )
          MailerPlugin.send(mail)
        case None =>
      }
    }
  }

  def orderConfirmation(order: OrderInfo)(implicit request: Request[Any]) = Akka.system.scheduler.scheduleOnce(1.second) {
    val deliveryInfo = order.deliveryInfo
    cityRepository.getByHostname(request.host).map { city =>
      sendToClient(systemEmail, deliveryInfo.email, order, city)
      adminEmails.map(email => sendToAdmins(email, deliveryInfo.email, systemEmail, order, city))
    }
  }

  private def sendToClient(from: String, to: String, order: OrderInfo, city: CityInfo)(implicit request: Request[Any]) = {
    val mail = Email(
      subject = getEmailSubject(order, city),
      to = Seq(to),
      from = from,
      bodyHtml = Some(views.html.emails.plain.order.notification(order).body)
    )
    MailerPlugin.send(mail)
  }
  private def sendToAdmins(adminEmail: String, userEmail: String, systemEmail: String, order: OrderInfo, city: CityInfo)
    (implicit request: Request[Any]) = {
    val mail = Email(
      subject = getEmailSubject(order, city),
      from = systemEmail,
      replyTo = Some(userEmail),
      to = Seq(adminEmail),
      bodyHtml = Some(views.html.emails.plain.order.adminNotification(order, idMark(city, order)).body)
    )
    MailerPlugin.send(mail)
  }
  private def getEmailSubject(order: OrderInfo, city: CityInfo): String  = {
    val orderDetails =
      if(order.items.length > 1){
        val tailLength = order.items.tail.length

        val itemSuffix = if (tailLength > 1) {
          tailLength % 10 match {
            case l if l > 1 && l < 5 => "а"
            case l if l > 5 => "ов"
          }
        } else {
          ""
        }
        order.items.head.title + Messages("emailhelper.orderconfirmation.subjectDetails", tailLength, itemSuffix)
      }
      else
        order.items.head.title
    Messages("emailhelper.orderconfirmation.subject", idMark(city, order), orderDetails)
  }
}
