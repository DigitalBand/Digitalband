package controllers

import play.api.mvc.{Action, Controller}
import models._
import com.google.inject.Inject
import dao.common.{CategoryRepository, ProductRepository}

class Category @Inject()(val productRepository: ProductRepository, val categoryRepository: CategoryRepository) extends Controller {
  def list = Action {
    val products: Seq[ProductUnit] = productRepository.getList(categoryRepository.get(1))
    Ok(views.html.Category.list(products))
  }
}
