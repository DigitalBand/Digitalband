package helpers

object disposable {
  def apply[A <: {def dispose(): Unit}, B](param: A)(f: A => B) : B = {
    try {
      f(param)
    } finally {
      param.dispose()
    }
  }
}
object closable {
  def apply[A <: {def close(): Unit}, B](param: A)(f: A => B) : B = {
    try {
      f(param)
    } finally {
      param.close()
    }
  }
}
