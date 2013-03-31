package controllers.admin

import com.google.inject.Inject
import dao.common.{QuestionRepository, ProductRepository, UserRepository}
import controllers.common.ControllerBase
import helpers.{EmailHelper, Secured}
import play.api.data._
import play.api.data.Forms._

class Question @Inject()(implicit userRepository: UserRepository, productRepository: ProductRepository, questionRepository: QuestionRepository) extends ControllerBase with Secured {
  val commentForm = Form("comment" -> text)
  def available(questionId: Int) = withAdmin { implicit user => implicit request =>
    val question = questionRepository.get(questionId)
    Ok(views.html.Admin.Question.available(question))
  }

  def unavailable(questionId: Int) = withAdmin { implicit user => implicit request =>
    val question = questionRepository.get(questionId)
    Ok(views.html.Admin.Question.unavailable(question))
  }
  def setUnavailable(questionId: Int) = withAdmin { implicit user => implicit ruquest =>
    val comment = commentForm.bindFromRequest.get
    val question = questionRepository.get(questionId)
    new EmailHelper().answerAvailability(s"${question.productTitle} нет в наличии", comment, question.email)
    questionRepository.setAnswered(questionId)
    Redirect(controllers.admin.routes.Dashboard.index())
  }
  def setAvailable(questionId: Int) = withAdmin { implicit user => implicit request =>
    val comment = commentForm.bindFromRequest.get
    val question = questionRepository.get(questionId)
    new EmailHelper().answerAvailability(s"${question.productTitle} есть в наличии", comment, question.email)
    questionRepository.setAnswered(questionId)
    Redirect(controllers.admin.routes.Dashboard.index())
  }
}
