package controllers

import com.google.inject.Inject
import controllers.common.ControllerBase
import dao.common.{ShopRepository, UserRepository}
import models.ProductDetails
import play.api.mvc.Action

class PriceList @Inject()(implicit userRepository: UserRepository, shopRepository: ShopRepository) extends ControllerBase {
  def forYandex = Action {

    val w = new java.io.StringWriter()
    val yandexShopInfo = shopRepository.getYandexShopInfo
    val yandex = views.html.PriceList.forYandex(
      Seq(ProductDetails(Some(1), "test", "test", "test", 12, "test", 1, isAvailable = true)),
      yandexShopInfo,
      (id) => {id.toString},
      (id) => {id.toString}
    ).toString


    //xml.XML.write(w, yandex, "UTF-8", xmlDecl = true, doctype =
      //xml.dtd.DocType("yml_catalog", xml.dtd.SystemID("shops.dtd"), Nil))
    Ok(yandex.trim).withHeaders(CONTENT_TYPE -> "text/xml")
  }
}
