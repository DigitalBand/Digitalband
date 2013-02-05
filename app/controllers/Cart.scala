package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.CartRepository

class Cart @Inject()(val cartRepository: CartRepository) extends Controller{
  def add(productId: Int, count: Int = 1) = Action {
    NotImplemented
  }
  def display = Action {
    NotImplemented
  }
  def delete(productId: Int) = Action {
    NotImplemented
  }
  def update(productId: Int, count: Int) = Action {
    NotImplemented
  }
}
