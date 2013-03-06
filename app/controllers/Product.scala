package controllers

import common.ControllerBase
import play.api.mvc.{Request, AnyContent, Action}
import com.google.inject.Inject
import dao.common._

import play.api.Play.current
import play.api.cache.Cached

class Product @Inject()(productRepository: ProductRepository,
                        categoryRepository: CategoryRepository,
                        imageRepository: ImageRepository,
                        brandRepository: BrandRepository,
                         ur:UserRepository) extends ControllerBase(ur) {

  def list = filteredList(1)

  def filteredList(categoryId: Int,
                   pageNumber: Int = 1,
                   brandId: Int = 0,
                   brandPage: Int = 1,
                   productId: Int = 0,
                   pageSize: Int = 10,
                   search: String = "") =
    //Cached(req => req.uri, 82000) {
      Action {
        implicit request =>
          if (productId > 0)
            display(productId, categoryId, brandId, brandPage, pageNumber, search)
          else {
            val products = productRepository.getList(categoryRepository.get(categoryId), brandId, pageNumber, pageSize, search)
            if (categoryId == 1 && !search.isEmpty && products.totalCount == 1)
              Redirect(routes.Product.display(products.items.head.id))
            else
              Ok(views.html.Product.list(
                products,
                categoryRepository.get(categoryId),
                categoryRepository.list(categoryId, brandId, search),
                brandRepository.list(categoryRepository.get(categoryId), brandPage, pageSize = 24, search),
                brandRepository.get(brandId),
                pageNumber,
                pageSize,
                categoryRepository.getBreadcrumbs(categoryId, productId, search),
                search))
          }
      }
    //}

  def display(id: Int): Action[AnyContent] =
    //Cached(req => req.toString, 82000) {
      Action {
        implicit request =>
        display(id, 1, 0, 1, 1, "")
      }
    //}

  def display(id: Int, categoryId: Int, brandId: Int, brandPage: Int, pageNumber: Int, search: String)(implicit request:Request[AnyContent]) = {
    Ok(views.html.Product.display(
      productRepository.get(id, brandRepository.get),
      imageRepository.listByProductId(id),
      categoryRepository.list(categoryId, brandId, search),
      brandRepository.list(categoryRepository.get(categoryId), brandPage, pageSize = 24, search),
      categoryId, brandId,
      categoryRepository.getBreadcrumbs(categoryId, id, ""), pageNumber, search))
  }
}

