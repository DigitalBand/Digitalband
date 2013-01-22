package controllers

import play.api.mvc.{Action, Controller}
import models._
import com.google.inject.Inject
import dao.common.{CategoryRepository, ProductRepository}

class Category @Inject()(val productRepository: ProductRepository, val categoryRepository: CategoryRepository) extends Controller {

}
