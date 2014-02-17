package dao.common

import models.DealerInfo

/**
 * Created by tim on 17/02/14.
 */
trait DealerRepository {
  def list: Seq[DealerInfo]
}
