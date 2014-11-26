package dao.common

import models.{ListPage, Question}

trait QuestionRepository {
  def setAnswered(questionId: Int)

  def get(id: Int):Question

  def listUnanswered(): Seq[Question]

  def insertQuestion(productId: Int, email: String): Option[Int]

  def listWithAnswers(pageNumber:Int, pageSize: Int = 20): ListPage[Question]
}
