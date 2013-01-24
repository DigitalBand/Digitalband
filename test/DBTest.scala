import dao.impl.orm.slick.tables.{CategoryImagesTable, CategoriesTable}
import models.{CategoryEntity}
import org.specs2.mutable.Specification
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession


class DBTest extends Specification {
  val dburl = "jdbc:mysql://localhost:3306/dbs?useEncoding=true&characterEncoding=UTF-8&allowMultiQueries=true"
  val user = "play"
  val password = "play"
  val driver = "com.mysql.jdbc.Driver"
  def database = Database.forURL(dburl, user, password, driver = driver)
  "DB" should {

    "get list of categories" in {
      database withSession {
        val categories = for {
          c <- CategoriesTable
          ci <- CategoryImagesTable if c.id === ci.categoryId
        } yield (c.id, c.title, ci.imageId)
        val cat = categories.list.map {
          image => image match {
              case (id: Int, title: String, imageId: Int) => CategoryEntity(id, title, imageId)
            }
        }
        val category = cat.head
        //category.title must beEqualTo("Каталог")
        //category.id must beEqualTo(1)
        category.imageId must beEqualTo(19066)
      }
    }



  }
}
