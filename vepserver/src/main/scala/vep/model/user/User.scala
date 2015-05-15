package vep.model.user

import spray.json.DefaultJsonProtocol
import vep.model.common.{ErrorCodes, VerifiableMultiple}
import vep.utils.StringUtils


case class User(uid: Int, email: String, password: String, salt: String, firstName: String, lastName: String, city: Option[String])

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

object UserImplicits extends DefaultJsonProtocol {
  implicit val impUser = jsonFormat7(User)
  implicit val impUserRegistration = jsonFormat(UserRegistration.apply, "email", "firstName", "lastName", "password", "city")
}