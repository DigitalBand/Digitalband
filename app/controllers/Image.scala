package controllers

import play.api.mvc.{Action, Controller}

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 13/01/2013
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
object Image extends Controller {
  def get(productId: Int, imageNumber: Int = 1) = Action {
    //ImageRepository.get(productId, imageNumber)
    Ok("")
  }
}
