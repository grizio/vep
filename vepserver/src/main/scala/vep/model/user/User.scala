package vep.model.user

import spray.http.DateTime
import spray.json.DefaultJsonProtocol
import vep.model.JsonImplicits
import vep.model.common.{VerifiableUnique, ErrorCodes, VerifiableMultiple}
import vep.utils.StringUtils


case class User(uid: Int, email: String, password: String, salt: String, firstName: String, lastName: String, city: Option[String], keyLogin: Option[String], expiration: Option[DateTime])

case class UserRegistration(email: String, firstName: String, lastName: String, password: String, city: Option[String]) extends VerifiableMultiple {
  override def verify: Boolean = {
    if (StringUtils.isBlank(email)) {
      addError("email", ErrorCodes.emptyEmail)
    } else if (StringUtils.isNotEmail(email)) {
      addError("email", ErrorCodes.invalidEmail)
    }
    if (StringUtils.isBlank(firstName)) {
      addError("firstName", ErrorCodes.emptyFirstName)
    }
    if (StringUtils.isBlank(lastName)) {
      addError("lastName", ErrorCodes.emptyLastName)
    }
    if (StringUtils.isEmpty(password)) {
      addError("password", ErrorCodes.emptyPassword)
    } else if (StringUtils.isNotSecure(password, needLowercaseLetter = false, needUppercaseLetter = false, needSymbol = false, minLength = 8)) {
      addError("password", ErrorCodes.weakPassword)
    }

    hasNotErrors
  }
}

case class UserLogin(email: String, password: String) extends VerifiableUnique {
  override def verify: Boolean = true // No test for field
}

object UserImplicits extends JsonImplicits {
  implicit val impUser = jsonFormat(User.apply, "uid", "email", "password", "salt", "firstName", "lastName", "city", "keyLogin", "expiration")
  implicit val impUserRegistration = jsonFormat(UserRegistration.apply, "email", "firstName", "lastName", "password", "city")
  implicit val impUserLogin = jsonFormat(UserLogin.apply, "email", "password")
}