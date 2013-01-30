package helpers.config

import com.tzavellas.sse.guice.ScalaModule
import dao.impl.orm.slick.{BrandRepository, CategoryRepository, ImageRepository, ProductRepository}

class InjectionModule extends ScalaModule {
  def configure() {
    bind[dao.common.ProductRepository].toInstance(new ProductRepository())
    bind[dao.common.ImageRepository].toInstance(new ImageRepository())
    bind[dao.common.CategoryRepository].toInstance(new CategoryRepository)
    bind[dao.common.BrandRepository].toInstance(new BrandRepository)
  }
}
