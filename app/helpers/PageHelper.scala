package helpers

object PageHelper {
  val pageRepository = db.Global.getControllerInstance(classOf[dao.common.PageRepository])
  def getPages() = pageRepository.list
}
