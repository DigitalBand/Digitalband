package dao.impl.orm.slick

import com.codahale.jerkson.Json
import models.AboutInfo
import dao.impl.orm.slick.common.RepositoryBase

class AboutRepository(pageRepository: PageRepository) extends RepositoryBase with dao.common.AboutRepository {
  val pageName = "about"

  def get(): AboutInfo = {
    val content = pageRepository.get(pageName)
    val aboutInfo =
      if (content.isDefined)
        Json.parse[AboutInfo](content.get)
    else
      new AboutInfo("", "")
    aboutInfo
  }

  def save(aboutInfo: AboutInfo) = {
    val jsonInfo = Json.generate(aboutInfo)
    val currentAboutInfo = get()
    if(currentAboutInfo.about == "" && currentAboutInfo.legalInfo == "")
      pageRepository.add(pageName, jsonInfo)
    else
      pageRepository.update(pageName, jsonInfo)
  }
}

