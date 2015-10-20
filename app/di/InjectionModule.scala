package di

import com.google.inject.AbstractModule
import dao.impl.orm.slick._
import play.api.Play
import webServices.{FakeImageSearch, GoogleImageSearch}

class InjectionModule extends AbstractModule {

  def configure() {
    bind(classOf[dao.common.ProductRepository]).toInstance(new ProductRepository)
    bind(classOf[dao.common.ImageRepository]).toInstance(new ImageRepository)
    bind(classOf[dao.common.CategoryRepository]).toInstance(new CategoryRepository)
    bind(classOf[dao.common.BrandRepository]).toInstance(new BrandRepository)
    bind(classOf[dao.common.UserRepository]).toInstance(new UserRepository)
    bind(classOf[dao.common.CartRepository]).toInstance(new CartRepository)
    bind(classOf[dao.common.OrderRepository]).toInstance(new OrderRepository)
    bind(classOf[dao.common.StockItemRepository]).toInstance(new StockItemRepository)
    bind(classOf[dao.common.DealerRepository]).toInstance(new DealerRepository)
    bind(classOf[dao.common.ShopRepository]).toInstance(new ShopRepository)
    bind(classOf[dao.common.CityRepository]).toInstance(new CityRepository)
    bind(classOf[dao.common.PageRepository]).toInstance(new PageRepository)
    bind(classOf[webServices.common.ImageSearch]).toInstance(new GoogleImageSearch)
    /*Play.current.configuration.getString("webservices.imageSearch") match {
      case Some(config) if config == "google" => bind(classOf[webServices.common.ImageSearch]).toInstance(new GoogleImageSearch)
      case _ => bind(classOf[webServices.common.ImageSearch]).toInstance(new FakeImageSearch)
    }*/
  }
}
