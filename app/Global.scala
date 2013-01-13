import com.google.inject.Guice
import dao.common.ProductRepository
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
    bind[ProductRepository].toInstance(ProductRepository)
  }
}