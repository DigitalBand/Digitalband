package views.html.common

import views.html.helper.{FieldElements, FieldConstructor}

package object bootstrap {
  implicit val fc = new FieldConstructor {
    def apply(elements: FieldElements) =
      bootstrap.fieldConstructor(elements)
  }
}
