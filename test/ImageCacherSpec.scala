package test
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import helpers.ImageCacher

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 13/01/2013
 * Time: 21:19
 * To change this template use File | Settings | File Templates.
 */
class ImageCacherSpec extends Specification {
  "Image Cacher" should {
    "combine paths" in {
      running(FakeApplication()) {
        val imgCacher = new ImageCacher("")
        val path = imgCacher.combine("/user/tim", List("product", "12", "2"))
        path.toString must beEqualTo("/user/tim/product/12/2")
      }

    }
  }

}
