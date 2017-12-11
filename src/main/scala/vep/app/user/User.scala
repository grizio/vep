package vep.app.user

import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt
import spray.json.{JsonFormat, RootJsonFormat}
import vep.framework.utils.JsonProtocol

case class User(
  id: String,
  email: String,
  password: String,
  role: UserRole.Value,
  authentications: Seq[Authentication],
  activationKey: Option[String],
  resetPasswordKey: Option[String]
)

object User {
  import JsonProtocol._

  implicit val userFormat: RootJsonFormat[User] = jsonFormat7(User.apply)
}

object UserRole extends Enumeration {
  val user, admin = Value

  import JsonProtocol._

  implicit val userRoleFormat: RootJsonFormat[UserRole.Value] = enumFormat(this, withName)

  def fromString(value: String): Option[UserRole.Value] = {
    values.find(_.toString == value)
  }

  private[user] def deserialize(value: String): UserRole.Value = {
    fromString(value).get
  }
}

case class Authentication(
  token: String,
  date: DateTime
) {
  def crypt(): Authentication = copy(
    token = BCrypt.hashpw(token, BCrypt.gensalt())
  )
}

object Authentication {
  import JsonProtocol._

  implicit val authenticationFormat: JsonFormat[Authentication] = jsonFormat2(Authentication.apply)
  implicit val authenticationSeqFormat: JsonFormat[Seq[Authentication]] = seqFormat(authenticationFormat)
}

case class UserView(
  id: String,
  email: String,
  firstName: String,
  lastName: String
)

object UserView {
  import JsonProtocol._

  implicit val userViewFormat: JsonFormat[UserView] = jsonFormat4(UserView.apply)
}