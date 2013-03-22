package helpers

import models.ReCaptcha
import java.util
import org.jsoup.Jsoup
case class RecaptchaChallenge(val challenge: String, val imageUrl: String)
object ReCaptchaHelper {
  def validate(recaptcha: ReCaptcha): Boolean = {
    val map: util.HashMap[String, String] = new util.HashMap[String, String]()
    map.put("challenge", recaptcha.challenge)
    map.put("response", recaptcha.response)
    map.put("privatekey", recaptcha.privatekey)
    map.put("remoteip", recaptcha.remoteip)
    val d = Jsoup.connect("http://www.google.com/recaptcha/api/verify")
      .data(map).timeout(10000).post().body().html()
    d contains "success"
  }
  def get(key: String) = {
    val baseUrl = "http://www.google.com/recaptcha/api/"
    val url = s"${baseUrl}noscript?k=$key";
    val d = Jsoup.connect(url).timeout(10000).get()
    val challenge = d.select("#recaptcha_challenge_field").`val`
    val imageUrl = baseUrl + d.select("img").attr("src")
     RecaptchaChallenge(challenge, imageUrl)
  }
}
