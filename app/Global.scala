import com.google.inject.Guice

import play.api.GlobalSettings
import com.tzavellas.sse.guice.ScalaModule
import dao.impl.fake._

object Global extends GlobalSettings {
  private lazy val injector = Guice.createInjector(new InjectionModule)


  override def getControllerInstance[A](controllerClass: Class[A]) = {
    injector.getInstance(controllerClass)
  }

}

class InjectionModule extends ScalaModule {
  def configure() {
    bind[dao.common.ProductRepository].toInstance(new ProductRepository())
    bind[dao.common.ImageRepository].toInstance(new dao.impl.orm.slick.ImageRepository())
    bind[dao.common.CategoryRepository].toInstance(new dao.impl.orm.slick.CategoryRepository)
  }
}