package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CategoryRepository, ProductRepository, ShopRepository, UserRepository}
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormat
import play.api.mvc.{AnyContent, Request, Action}

class PriceList @Inject()(implicit userRepository: UserRepository, shopRepository: ShopRepository, productRepository: ProductRepository, categoryRepository: CategoryRepository) extends ControllerBase {
  def host(implicit request: Request[AnyContent]) = request.host
  def forYandex = Action {
    implicit request =>
      val w = new java.io.StringWriter()
      val yandexShopInfo = shopRepository.getYandexShopInfo
      val products = productRepository.listAll(host)
      val categories = categoryRepository.listAll
      val fmt = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")
      val getUrl: Int => String = { id => routes.Product.display(id).url }
      val getPictureUrl: Int => String = { id => routes.Image.get(id.toString + ".jpg", 90, "150x150", "full").url}
      val yandex = views.html.PriceList.forYandex(
        products,
        categories,
        fmt.print(DateTime.now().withZone(DateTimeZone.forID("Europe/Moscow"))),
        yandexShopInfo,
        getUrl,
        getPictureUrl,
        request.host
      ).toString()
      Ok(yandex.trim).withHeaders(CONTENT_TYPE -> "text/xml")
  }

}
