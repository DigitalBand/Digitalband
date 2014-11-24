package helpers

import dao.common.{UserRepository, QuestionRepository, OrderRepository}


class NotificationsHelper(orderRepository: OrderRepository, questionRepository: QuestionRepository, userRepository: UserRepository) {
  def unansweredQuestions() = {
    val unansweredCount = questionRepository.countUnanswered
    if (unansweredCount.size > 0) {
      implicit val ur = userRepository
      val emailHelper = new EmailHelper()
      emailHelper.sendUnansweredQuestionsExist(unansweredCount)
    }
  }

  def unconfirmedOrders() = {
    val unconfirmedCount = orderRepository.countUnconfirmed
    if (unconfirmedCount.size > 0) {
      implicit val ur = userRepository
      val emailHelper = new EmailHelper()
      emailHelper.sendUnconfirmedOrdersExist(unconfirmedCount)
    }
  }

}
