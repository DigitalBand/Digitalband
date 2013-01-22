package controllers

import common.ControllerBase
import play.api.mvc.Action
import com.google.inject.Inject
import dao.common._

class Product @Inject()(productRepository: ProductRepository, categoryRepository: CategoryRepository, imageRepository: ImageRepository, brandRepository: BrandRepository) extends ControllerBase {
  def list = Action {
    Ok(views.html.Product.list(productRepository.getList(categoryRepository.get(1)), 1))
  }
  def filteredList(categoryId: Int, pageNumber: Int) = Action {
    implicit request =>
      val brandId: Int = intParam("brandId")
      val brandPage: Int = intParam("brandPage")
      val productId: Int = intParam("productId")
      if (productId > 0)
        Ok(views.html.Product.display(productRepository.get(productId), imageRepository.listByProductId(productId)))
      else {
        val brands = brandRepository.list(categoryRepository.get(categoryId), brandPage)
        Ok(views.html.Product.list(productRepository.getList(categoryRepository.get(categoryId), brandId, pageNumber, 10), categoryId))
      }
  }


}
