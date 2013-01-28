package controllers

import common.ControllerBase
import play.api.mvc.Action
import com.google.inject.Inject
import dao.common._
import models.{BrandEntity, ListPage, ProductEntity}

class Product @Inject()(productRepository: ProductRepository, categoryRepository: CategoryRepository, imageRepository: ImageRepository, brandRepository: BrandRepository) extends ControllerBase {
  def list = Action {
    val brands = brandRepository.list(categoryRepository.get(1), 1, 5)
    val categories = categoryRepository.list(1, 0)
    Ok(views.html.Product.list(productRepository.getList(categoryRepository.get(1)), categoryRepository.get(1),
      categories,
      brands,
      None, 1))
  }
  def filteredList(categoryId: Int, pageNumber: Int, brandId: Int = 0, brandPage: Int = 1, productId: Int = 0) = Action {
    implicit request =>
      if (productId > 0)
        Ok(views.html.Product.display(productRepository.get(productId, brandRepository.get), imageRepository.listByProductId(productId)))
      else {
        val brands = brandRepository.list(categoryRepository.get(categoryId), brandPage, 5)
        val products = productRepository.getList(categoryRepository.get(categoryId), brandId, pageNumber, 10)
        val categories = categoryRepository.list(categoryId, brandId)
        val brand = brandRepository.get(brandId)
        Ok(views.html.Product.list(products, categoryRepository.get(categoryId), categories, brands, brand, pageNumber))
      }
  }

}

