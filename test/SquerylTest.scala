package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.FakeApplication
import org.squeryl.adapters.MySQLAdapter
import dao.impl.orm.squeryl.CategoryRepository

class SquerylTest extends Specification {

  import org.squeryl._

  Class.forName("com.mysql.jdbc.Driver");

  SessionFactory.concreteFactory = Some(() =>
    Session.create(
      java.sql.DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/dbs?useEncoding=true&characterEncoding=UTF-8&allowMultiQueries=true",
        "play", "play"),
      new MySQLAdapter))
  "Squeryl" should {
    "get category" in {
      val repo = new CategoryRepository
      val category = repo.get(1)
      category.id must equalTo(1)
    }
    "get categories with pictures" in {
      val repo = new CategoryRepository
      var categories = repo.listWithPictures()
      categories.length must beGreaterThan(1)
    }
  }
}
