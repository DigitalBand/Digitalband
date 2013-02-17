import com.google.inject.Guice

import helpers.config.InjectionModule
import play.api._


object Global extends GlobalSettings {
  private lazy val injector = Guice.createInjector(new InjectionModule)

  override def getControllerInstance[A](controllerClass: Class[A]) = {
    injector.getInstance(controllerClass)
  }
}

