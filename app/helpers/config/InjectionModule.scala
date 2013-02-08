package helpers.config

import com.tzavellas.sse.guice.ScalaModule
import dao.impl.orm.slick._
import dao.impl.fake.{UserRepository}

class InjectionModule extends ScalaModule {
  def configure() {
    bind[dao.common.ProductRepository].toInstance(new ProductRepository())
    bind[dao.common.ImageRepository].toInstance(new ImageRepository())
    bind[dao.common.CategoryRepository].toInstance(new CategoryRepository)
    bind[dao.common.BrandRepository].toInstance(new BrandRepository)
    bind[dao.common.UserRepository].toInstance(new UserRepository)
    bind[dao.common.CartRepository].toInstance(new CartRepository)
  }
}
