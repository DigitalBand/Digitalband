import dao.impl.orm.slick.CategoryRepository
import org.specs2.mutable.Specification
import play.api.test.FakeApplication
import play.api.test._
import play.api.test.Helpers._

class SlickRepositoryTest extends Specification {
    "Repository" should {
      "return results from real db" in {
        running(FakeApplication()){
          val repository = new CategoryRepository()
          val categories = repository.getListWithPictures
          categories.length must beGreaterThan(5)
        }
      }
    }
}
