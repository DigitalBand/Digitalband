package controllers

import play.api.mvc.{Action, Controller}
import models._
import com.google.inject.Inject
import dao.common.ProductRepository

class Category @Inject()(val productRepository: ProductRepository) extends Controller {
  def list = Action {
    val products: Seq[ProductUnit] = productRepository.getList()
    Ok(views.html.Category.list(products))
  }
}
