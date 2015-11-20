package dao.common

import models.DealerInfo

import scala.concurrent.Future

trait DealerRepository {
  def list: Future[Seq[DealerInfo]]
}
