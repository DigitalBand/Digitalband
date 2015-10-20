package views.html.common

import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import views.html.helper.{FieldElements, FieldConstructor}

package object bootstrap {
  implicit val fc = new FieldConstructor {
    def apply(elements: FieldElements) =
      bootstrap.fieldConstructor(elements)
  }

  implicit val hfc = new FieldConstructor {
    def apply(elements: FieldElements) =
      bootstrap.horizontalFieldConstructor(elements)
  }
}
