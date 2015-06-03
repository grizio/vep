package vep.model.user

import spray.http.DateTime
import spray.json.{JsString, JsArray, JsValue, RootJsonFormat}
import vep.model.JsonImplicits
import vep.model.common.{ErrorCodes, VerifiableMultiple, VerifiableUnique}
import vep.utils.StringUtils


case class User(uid: Int, email: String, password: String, salt: String, firstName: String, lastName: String,
                city: Option[String]=None, keyLogin: Option[String]=None, expiration: Option[DateTime]=None, roles: Seq[String]=Seq())

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

case class RolesSeq(roles: Seq[String])

object UserImplicits extends JsonImplicits {
  implicit val impUser = jsonFormat(User.apply, "uid", "email", "password", "salt", "firstName", "lastName", "city", "keyLogin", "expiration", "roles")
  implicit val impUserRegistration = jsonFormat(UserRegistration.apply, "email", "firstName", "lastName", "password", "city")
  implicit val impUserLogin = jsonFormat(UserLogin.apply, "email", "password")

  implicit val impRolesSeq = new RootJsonFormat[RolesSeq] {
    def read(value: JsValue) = RolesSeq(value.convertTo[Seq[String]])
    def write(f: RolesSeq) = JsArray(f.roles map { role => JsString(role) }: _*)
  }
}
