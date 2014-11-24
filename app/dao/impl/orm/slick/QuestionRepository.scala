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
        q.id, q.product_id, p.title, q.email, q.type
      from
        questions q
      inner join products p on p.id = q.productId
      where q.id = $id;
    """.as[Question].first
  }

  def setAnswered(questionId: Int) = database withDynSession {
     sqlu"update questions set status = 'answered' where id = $questionId;".execute
  }

  def insertQuestion(productId: Int, email: String): Option[Int] = database withDynSession {
    val questionType = "availability"
    val unansweredStatus = "unanswered"
    val count = sql"""
      select count(*) from questions
      where
        product_id = $productId and
        email = $email and
        type = $questionType and
        status = $unansweredStatus
    """.as[Int].first()
    if (count > 0)
      None
    else {
      sqlu"insert into questions(product_id, email, type) values($productId, $email, $questionType)".execute
      sql"select last_insert_id();".as[Int].firstOption
    }
  }

  def listUnanswered(): Seq[Question] = database withDynSession {
    implicit val getQuestion = GetResult(r => new Question(r.<<, r.<<, r.<<, r.<<, r.<<))
    sql"""
      select
        q.id, q.product_id, p.title, q.email, q.type
      from
        questions q
      inner join products p on p.id = q.product_id
      where status = 'unanswered';
    """.as[Question].list
  }

  def listWithAnswers(pageNumber: Int, pageSize: Int) = database withDynSession {
    implicit val getQuestion = GetResult(r => new Question(r.<<, r.<<, r.<<, r.<<, r.<<))
    val items = sql"""
      select
        q.id, q.product_id, p.title, q.email, q.type
      from
        questions q
      inner join products p on p.id = q.product_id
      limit ${pageSize * (pageNumber - 1)}, ${pageSize};
    """.as[Question].list
    val totalCount = sql"select count(q.id) from questions q".as[Int].first()
    new ListPage(pageNumber, items, totalCount)
  }

  def countUnanswered(): Map[String, Int] = database withDynSession {
    Map("Host" -> 1, "other" -> 5)
  }
}