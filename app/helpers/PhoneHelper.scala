package helpers

object PhoneHelper {
  def parsePhones(phones: Option[String]): Seq[String] = phones match {
    case Some(phones) => phones.split(";")
    case None => Nil
  }
}
