import com.google.inject.Guice

import com.tzavellas.sse.guice.ScalaModule
import dao.impl.orm.slick._

import helpers.EmailHelper
import play.api._
import play.api.Play.current
import webServices.{GoogleImageSearch, FakeImageSearch}

object Global extends GlobalSettings {
  private lazy val injector = Guice.createInjector(new InjectionModule)

  override def getControllerInstance[A](controllerClass: Class[A]) = {
    injector.getInstance(controllerClass)
  }

  override def onStart(app: Application) = {
    import play.api.libs.concurrent.Akka
    import play.api.libs.concurrent.Execution.Implicits._
    import scala.concurrent.duration._

    Akka.system.scheduler.schedule(0.seconds, 2.hours) {
      val or: dao.common.OrderRepository = new OrderRepository()
      val unconfirmedCount: Int = or.countUnconfirmed
      if (unconfirmedCount > 0) {
        implicit val ur = new UserRepository
        val emailHelper = new EmailHelper()
        emailHelper.sendUnconfirmedOrdersExist(unconfirmedCount)
      }
    }
  }
}

class InjectionModule extends ScalaModule {
  def configure() {
    bind[dao.common.ProductRepository].toInstance(new ProductRepository())
    bind[dao.common.ImageRepository].toInstance(new ImageRepository())
    bind[dao.common.CategoryRepository].toInstance(new CategoryRepository)
    bind[dao.common.BrandRepository].toInstance(new BrandRepository)
    bind[dao.common.UserRepository].toInstance(new UserRepository)
    bind[dao.common.CartRepository].toInstance(new CartRepository)
    bind[dao.common.OrderRepository].toInstance(new OrderRepository)
    Play.current.configuration.getString("webservices.imageSearch") match {
      case Some(config) if config == "google" => bind[webServices.common.ImageSearch].toInstance(new GoogleImageSearch)
      case _ => bind[webServices.common.ImageSearch].toInstance(new FakeImageSearch)
    }
  }
}

