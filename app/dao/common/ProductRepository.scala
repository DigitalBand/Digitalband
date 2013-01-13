package dao.common

import models.ProductUnit

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 13/01/2013
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
trait ProductRepository {
  def getList(): List[ProductUnit]

}
