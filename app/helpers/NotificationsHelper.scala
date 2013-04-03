package helpers

import dao.common.{UserRepository, QuestionRepository, OrderRepository}


class NotificationsHelper(orderRepository: OrderRepository, questionRepository: QuestionRepository, userRepository: UserRepository) {
  def unansweredQuestions() = {
    val unansweredCount: Int = questionRepository.listUnanswered().length
    if (unansweredCount > 0) {
      implicit val ur = userRepository
      val emailHelper = new EmailHelper()
      emailHelper.sendUnansweredQuestionsExist(unansweredCount)
    }
  }

  def unconfirmedOrders() = {
    val unconfirmedCount: Int = orderRepository.countUnconfirmed
    if (unconfirmedCount > 0) {
      implicit val ur = userRepository
      val emailHelper = new EmailHelper()
      emailHelper.sendUnconfirmedOrdersExist(unconfirmedCount)
    }
  }

}
