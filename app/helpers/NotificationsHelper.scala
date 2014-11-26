package helpers

import dao.common.{UserRepository, QuestionRepository, OrderRepository}

class NotificationsHelper(orderRepository: OrderRepository, questionRepository: QuestionRepository, userRepository: UserRepository) {
  def unconfirmedOrders() = {
    val unconfirmedOrders = orderRepository.countUnconfirmed
    if (unconfirmedOrders.size > 0) {
      implicit val ur = userRepository
      val emailHelper = new EmailHelper()
      emailHelper.sendUnconfirmedOrdersExist(unconfirmedOrders)
    }
  }

}
