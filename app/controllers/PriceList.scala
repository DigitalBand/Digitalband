package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{CategoryRepository, ProductRepository, ShopRepository, UserRepository}
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.DateTimeFormat
import play.api.mvc.Action

class PriceList @Inject()(implicit userRepository: UserRepository, shopRepository: ShopRepository, productRepository: ProductRepository, categoryRepository: CategoryRepository) extends ControllerBase {
  def forYandex = Action {

    val w = new java.io.StringWriter()
    val yandexShopInfo = shopRepository.getYandexShopInfo
    val products = productRepository.listAll
    val categories = categoryRepository.listAll
    val fmt = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")
    val getUrl: Int => String = { id => routes.Product.display(id).url}
    val getPictureUrl: Int => String = { id => routes.Image.get(id.toString + ".jpg", 90, "300x300", "full").url}
    val yandex = views.html.PriceList.forYandex(
      products,
      categories,
      fmt.print(DateTime.now().withZone(DateTimeZone.forID("Europe/Moscow"))),
      yandexShopInfo,
      getUrl,
      getPictureUrl
    ).toString()


    //xml.XML.write(w, yandex, "UTF-8", xmlDecl = true, doctype =
    //xml.dtd.DocType("yml_catalog", xml.dtd.SystemID("shops.dtd"), Nil))
    Ok(yandex.trim).withHeaders(CONTENT_TYPE -> "text/xml")
  }
}
