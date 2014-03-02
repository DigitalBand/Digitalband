package controllers.admin

import com.google.inject.Inject
import dao.common.{QuestionRepository, ProductRepository, UserRepository}
import controllers.common.ControllerBase
import helpers.{EmailHelper, Secured}
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._
import play.api.Play.current
import play.api.Logger

class Question @Inject()(implicit userRepository: UserRepository, productRepository: ProductRepository, questionRepository: QuestionRepository) extends ControllerBase with Secured {
  val commentForm = Form("comment" -> text)
  val emailHelper = new EmailHelper()
  def listWithAnswers(pageNumber: Int, pageSize: Int = 20) = withAdmin {
    implicit user => implicit request =>
      val questions = questionRepository.listWithAnswers(pageNumber, pageSize)
      Ok(views.html.Admin.Question.listWithAnswers(questions))
  }
  def available(questionId: Int) = withAdmin {
    implicit user => implicit request =>
      val question = questionRepository.get(questionId)
      Ok(views.html.Admin.Question.available(question))
  }

  def unavailable(questionId: Int) = withAdmin {
    implicit user => implicit request =>
      val question = questionRepository.get(questionId)
      Ok(views.html.Admin.Question.unavailable(question))
  }

  def setUnavailable(questionId: Int) = withAdmin {
    implicit user => implicit ruquest =>
      val comment = commentForm.bindFromRequest.get
      val question = questionRepository.get(questionId)
      Akka.system.scheduler.scheduleOnce(1.second) {
        try {
          emailHelper.answerAvailability(s"${question.productTitle} нет в наличии", comment, question.email)
          questionRepository.setAnswered(questionId)
        } catch {
          case e : Throwable => Logger.error("send email error", e)
        }
      }
      Redirect(controllers.admin.routes.Dashboard.index())
  }

  def setAvailable(questionId: Int) = withAdmin {
    implicit user => implicit request =>
      val comment = commentForm.bindFromRequest.get
      val question = questionRepository.get(questionId)
      Akka.system.scheduler.scheduleOnce(1.second) {
        try {
        emailHelper.answerAvailability(s"${question.productTitle} есть в наличии", comment, question.email)
        questionRepository.setAnswered(questionId)
        } catch {
          case e : Throwable => Logger.error("send email error", e)
        }
      }
      Redirect(controllers.admin.routes.Dashboard.index())
  }
}
