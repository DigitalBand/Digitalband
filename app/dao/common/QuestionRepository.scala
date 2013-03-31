package dao.common

import models.Question

trait QuestionRepository {
  def setAnswered(questionId: Int)

  def get(id: Int):Question

  def listUnanswered(): Seq[Question]

  def insertQuestion(productId: Int, email: String): Option[Int]

}
