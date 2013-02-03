package controllers

import common.ControllerBase
import play.api.mvc.{AnyContent, Result, Action}
import com.google.inject.Inject
import dao.common._
import models.{BrandEntity, ListPage, ProductEntity}

class Product @Inject()(productRepository: ProductRepository, categoryRepository: CategoryRepository, imageRepository: ImageRepository, brandRepository: BrandRepository) extends ControllerBase {

  def list = filteredList(1)

  def filteredList(categoryId: Int, pageNumber: Int = 1, brandId: Int = 0, brandPage: Int = 1, productId: Int = 0, pageSize: Int = 10) = Action {
    implicit request =>
      if (productId > 0)
        display(productId, categoryId, brandId, brandPage, pageNumber)
      else {
        Ok(views.html.Product.list(
          productRepository.getList(categoryRepository.get(categoryId), brandId, pageNumber, pageSize),
          categoryRepository.get(categoryId),
          categoryRepository.list(categoryId, brandId),
          brandRepository.list(categoryRepository.get(categoryId), brandPage, pageSize = 24),
          brandRepository.get(brandId),
          pageNumber,
          pageSize,
          categoryRepository.getBreadcrumbs(categoryId, productId)))
      }
  }

  def display(id: Int): Action[AnyContent] = Action {
      display(id, 1, 0, 1, 1)
  }
  def display(id: Int, categoryId: Int, brandId: Int, brandPage: Int, pageNumber:Int) = {
    Ok(views.html.Product.display(
      productRepository.get(id, brandRepository.get),
      imageRepository.listByProductId(id),
      categoryRepository.list(categoryId, brandId),
      brandRepository.list(categoryRepository.get(categoryId), brandPage, pageSize = 24),
      categoryId, brandId,
      categoryRepository.getBreadcrumbs(categoryId, id), pageNumber))
  }
}

