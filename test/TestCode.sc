import dao.ProductRepository

object session {
  val products = ProductRepository.getList()
  products.map { product =>
    product.price
  }


}