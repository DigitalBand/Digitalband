import com.google.inject.Guice

import org.squeryl.adapters.MySQLAdapter
import helpers.config.InjectionModule
import org.squeryl.{Session, SessionFactory}
import play.api.db.DB
import play.api._


object Global extends GlobalSettings {
  private lazy val injector = Guice.createInjector(new InjectionModule)

  override def getControllerInstance[A](controllerClass: Class[A]) = {
    injector.getInstance(controllerClass)
  }

  override def onStart(app: Application) {
    SessionFactory.concreteFactory = Some(() =>
      Session.create(DB.getConnection()(app), new MySQLAdapter))
  }

}

