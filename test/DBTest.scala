import models.{CategoryImages, CategoryEntity, CategoryTable}
import org.specs2.mutable.Specification
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

class DBTest extends Specification {
  "DB" should {

    "get list of categories" in {
      Database.forURL("jdbc:mysql://localhost:3306/dbs?useEncoding=true&characterEncoding=UTF-8&allowMultiQueries=true",
        driver = "com.mysql.jdbc.Driver", user = "play", password = "play") withSession {
        def getCategories = for {
          c <- CategoryTable
          ci <- CategoryImages if ci.imageId =!= null
        } yield (c.id, c.title, ci.imageId)
        def categoryId: String = ""
        categoryId must beEqualTo("")
      }
    }

  }
}
