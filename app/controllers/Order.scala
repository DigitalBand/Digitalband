package controllers

import play.api.mvc.{Action, Controller}
import com.google.inject.Inject
import dao.common.OrderRepository

class Order @Inject()(orderRepository: OrderRepository) extends Controller {
   def fill() = Action {
     NotImplemented
   }
}
