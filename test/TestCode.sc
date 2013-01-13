import dao.impl.fake.ProductRepository

object session {
  val products = ProductRepository.getList()
  val l: List[Product] = List(new models.ProductUnit("1", "", 1, 1))
  products.map { product =>
    product.price
  }


}