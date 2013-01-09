package models

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 05/01/2013
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
case class ReCaptcha(challenge: String, response: String, remoteip: String, privatekey: String)
