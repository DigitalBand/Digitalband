package db

import com.google.inject.Guice
import di.InjectionModule
import play.api._

object Global extends GlobalSettings {
  private lazy val injector = Guice.createInjector(new InjectionModule)

  def getControllerInstance[A](controllerClass: Class[A]) = {
    injector.getInstance(controllerClass)
  }

  override def onStart(app: Application) = {
    /*import play.api.libs.concurrent.Akka
    import play.api.libs.concurrent.Execution.Implicits._
    import scala.concurrent.duration._
    val notificationsHelper = new NotificationsHelper(new OrderRepository, new UserRepository)
    Akka.system.scheduler.schedule(0.seconds, 2.hours) {
      notificationsHelper.unconfirmedOrders()
    }*/
  }
}



