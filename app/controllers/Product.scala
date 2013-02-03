package controllers

import common.ControllerBase
import play.api.mvc.Action
import com.google.inject.Inject
import dao.common._
import models.{BrandEntity, ListPage, ProductEntity}

class Product @Inject()(productRepository: ProductRepository, categoryRepository: CategoryRepository, imageRepository: ImageRepository, brandRepository: BrandRepository) extends ControllerBase {
  val brandListCount = 24;
  def list = filteredList(1)

  def filteredList(categoryId: Int, pageNumber: Int = 1, brandId: Int = 0, brandPage: Int = 1, productId: Int = 0, pageSize: Int = 10) = Action {
    implicit request =>
      if (productId > 0)
        Ok(views.html.Product.display(productRepository.get(productId, brandRepository.get), imageRepository.listByProductId(productId)))
      else {
        Ok(views.html.Product.list(
          productRepository.getList(categoryRepository.get(categoryId), brandId, pageNumber, pageSize),
          categoryRepository.get(categoryId),
          categoryRepository.list(categoryId, brandId),
          brandRepository.list(categoryRepository.get(categoryId), brandPage, brandListCount),
          brandRepository.get(brandId),
          pageNumber,
          pageSize,
          categoryRepository.getBreadcrumbs(categoryId, productId)))
      }
  }

  def display(id: Int) = Action {
    Ok(views.html.Product.display(productRepository.get(id, brandRepository.get), imageRepository.listByProductId(id)))
  }
}

