package helpers

object using {
  /*
   * Recreate C#'s Using statement
   *
   * Use a method called apply, which in Scala can be invoked directly off the object, e.g.
   * Using(foo) instead of Using.apply(foo)
   */
  def apply[A <: {def dispose(): Unit}, B](param: A)(f: A => B) : B =
    try {
      f(param)
    } finally {
      param.dispose()
    }
}
