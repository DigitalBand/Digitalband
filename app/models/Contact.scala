package models

case class Contact(name: String, email: String, productName: String,
                   message: String, recaptcha_challenge_field: String, recaptcha_response_field: String)
