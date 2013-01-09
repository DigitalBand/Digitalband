package controllers

import play.api.mvc.{Action, Controller}
import dao.ProductRepository
import models._

object Category extends Controller {
  def list = Action {
    val products: List[Product] = ProductRepository.getList()
    Ok(views.html.Category.list(products))
  }
}
