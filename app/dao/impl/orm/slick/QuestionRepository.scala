package dao.impl.orm.slick


import common.RepositoryBase
import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import slick.jdbc.{StaticQuery => Q, GetResult}
import Q.interpolation
import models.{ListPage, Question}


class QuestionRepository extends RepositoryBase with dao.common.QuestionRepository {

  def get(id: Int):Question = database withDynSession {
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

  def setAnswered(questionId: Int) = database withDynSession {
     sqlu"update questions set status = 'answered' where questionId = $questionId;".execute
  }

  def insertQuestion(productId: Int, email: String): Option[Int] = database withDynSession {
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

  def listUnanswered(): Seq[Question] = database withDynSession {
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

  def listWithAnswers(pageNumber: Int, pageSize: Int) = database withDynSession {
    implicit val getQuestion = GetResult(r => new Question(r.<<, r.<<, r.<<, r.<<, r.<<))
    val items = sql"""
      select
        q.questionId, q.productId, p.title, q.email, q.type
      from
        questions q
      inner join products p on p.productId = q.productId
      limit ${pageSize * (pageNumber - 1)}, ${pageSize};
    """.as[Question].list
    val totalCount = sql"select count(q.questionId) from questions q".as[Int].first()
    new ListPage(pageNumber, items, totalCount)
  }
}