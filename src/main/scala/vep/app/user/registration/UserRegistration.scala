package vep.app.user.registration

import spray.json.RootJsonFormat
import vep.framework.utils.JsonProtocol

case class UserRegistration(
  email: String,
  password: String
)

object UserRegistration {
  import JsonProtocol._

  implicit val userCreateFormat: RootJsonFormat[UserRegistration] = jsonFormat2(UserRegistration.apply)
}