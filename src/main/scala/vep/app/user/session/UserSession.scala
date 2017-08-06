package vep.app.user.session

import spray.json.RootJsonFormat
import vep.app.user.UserRole
import vep.framework.utils.JsonProtocol

case class UserSession(
  email: String,
  role: UserRole.Value
)

object UserSession {
  import JsonProtocol._

  implicit val userSession: RootJsonFormat[UserSession] = jsonFormat2(UserSession.apply)
}

case class UserLogin(
  email: String,
  password: String
)

object UserLogin {
  import JsonProtocol._

  implicit val userLoginFormat: RootJsonFormat[UserLogin] = jsonFormat2(UserLogin.apply)
}

case class ResetPassword(
  email: String,
  token: String,
  password: String
)

object ResetPassword {
  import JsonProtocol._

  implicit val resetPasswordFormat: RootJsonFormat[ResetPassword] = jsonFormat3(ResetPassword.apply)
}