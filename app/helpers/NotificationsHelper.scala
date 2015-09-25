package helpers

import dao.common.{UserRepository, QuestionRepository, OrderRepository}
import scala.concurrent.ExecutionContext.Implicits.global

class NotificationsHelper(orderRepository: OrderRepository, questionRepository: QuestionRepository, userRepository: UserRepository) {
  def unconfirmedOrders() = orderRepository.groupUnconfirmedByHost.map { unconfirmedOrders =>
    if (unconfirmedOrders.nonEmpty) {
      implicit val ur = userRepository
      val emailHelper = new EmailHelper()
      emailHelper.sendUnconfirmedOrdersExist(unconfirmedOrders)
    }
  }
}
