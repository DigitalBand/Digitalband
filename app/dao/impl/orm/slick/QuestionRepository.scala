package dao.impl.orm.slick

import common.Profile.driver.simple._
import common.RepositoryBase
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import models.Question


class QuestionRepository extends RepositoryBase with dao.common.QuestionRepository {

  def get(id: Int):Question = database withSession {
    implicit val getQuestion = GetResult(r => new Question(r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        q.questionId, q.productId, p.title, q.email, q.type
      from
        questions q
      inner join products p on p.productId = q.productId
      where q.questionId = $id;
    """.as[Question].first
  }

  def setAnswered(questionId: Int) = database withSession {
     sqlu"update questions set status = 'answered' where questionId = $questionId;".execute
  }

  def insertQuestion(productId: Int, email: String): Option[Int] = database withSession {
    val questionType = "availability"
    val unansweredStatus = "unanswered"
    val count = sql"""
      select count(*) from questions
      where
        productId = $productId and
        email = $email and
        type = $questionType and
        status = $unansweredStatus
    """.as[Int].first()
    if (count > 0)
      None
    else {
      sqlu"insert into questions(productId, email, type) values($productId, $email, $questionType)".execute
      sql"select last_insert_id();".as[Int].firstOption
    }
  }

  def listUnanswered(): Seq[Question] = database withSession {
    implicit val getQuestion = GetResult(r => new Question(r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        q.questionId, q.productId, p.title, q.email, q.type
      from
        questions q
      inner join products p on p.productId = q.productId
      where status = 'unanswered';
    """.as[Question].list
  }
}