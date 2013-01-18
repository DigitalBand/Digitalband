package models

case class ReCaptcha(challenge: String, response: String, remoteip: String, privatekey: String)
