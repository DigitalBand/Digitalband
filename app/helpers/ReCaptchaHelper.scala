package helpers

import models.ReCaptcha
import java.util
import org.jsoup.Jsoup
import play.api.Play
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RecaptchaChallenge(val challenge: String, val imageUrl: String)

trait RecaptchaWrapper {
  def get(key: String): Future[RecaptchaChallenge]
  def validate(recaptcha: ReCaptcha): Boolean
}
class RecaptchaWrapperRealImpl extends RecaptchaWrapper {
  def get(key: String) = {
    val baseUrl = "http://www.google.com/recaptcha/api/"
    val url = s"${baseUrl}noscript?k=$key";
    val d = Jsoup.connect(url).timeout(10000).get()
    val challenge = d.select("#recaptcha_challenge_field").`val`
    val imageUrl = baseUrl + d.select("img").attr("src")
    Future(RecaptchaChallenge(challenge, imageUrl))
  }

  def validate(recaptcha: ReCaptcha) = {
    val map: util.HashMap[String, String] = new util.HashMap[String, String]()
    map.put("challenge", recaptcha.challenge)
    map.put("response", recaptcha.response)
    map.put("privatekey", recaptcha.privatekey)
    map.put("remoteip", recaptcha.remoteip)
    val d = Jsoup.connect("http://www.google.com/recaptcha/api/verify")
      .data(map).timeout(10000).post().body().html()
    d contains "success"
  }
}
class RecaptchaFalseImpl extends RecaptchaWrapper {
  def get(key: String) = Future(RecaptchaChallenge("sdfsdf", ""))

  def validate(recaptcha: ReCaptcha) = true
}
object ReCaptchaHelper {
  val recaptchaWrapper: RecaptchaWrapper = {
    Play.current.configuration.getString("recaptcha.impl") match {
      case Some(config) => if (config == "false") new RecaptchaFalseImpl else new RecaptchaWrapperRealImpl
      case None => new RecaptchaWrapperRealImpl
    }
  }
  def validate(recaptcha: ReCaptcha): Boolean = recaptchaWrapper.validate(recaptcha)
  def get(key: String) = recaptchaWrapper.get(key)
}
