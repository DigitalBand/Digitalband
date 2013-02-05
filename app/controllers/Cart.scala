package controllers

import play.api.mvc._
import com.google.inject.Inject
import dao.common.CartRepository

class Cart @Inject()(val cartRepository: CartRepository) extends Controller{
  def add(productId: Int, count: Int = 1) = ???
}
