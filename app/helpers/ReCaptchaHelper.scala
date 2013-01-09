package helpers

import models.ReCaptcha
import java.util
import org.jsoup.Jsoup

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
}
