package helpers

import dao.common.{UserRepository, OrderRepository}
import scala.concurrent.ExecutionContext.Implicits.global

class NotificationsHelper(orderRepository: OrderRepository, userRepository: UserRepository, emailHelper: EmailHelper) {
  def unconfirmedOrders() = orderRepository.groupUnconfirmedByHost.map { unconfirmedOrders =>
    if (unconfirmedOrders.nonEmpty) {
      implicit val ur = userRepository
      emailHelper.sendUnconfirmedOrdersExist(unconfirmedOrders)
    }
  }
}
