package vep.app.user

import java.time.LocalDateTime

import org.mindrot.jbcrypt.BCrypt
import scalikejdbc.WrappedResultSet
import spray.json.{JsonFormat, JsonParser, RootJsonFormat}
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

  def apply(rs: WrappedResultSet): User = new User(
    id = rs.string("id"),
    email = rs.string("email"),
    password = rs.string("password"),
    role = UserRole.deserialize(rs.string("role")),
    authentications = Authentication.authenticationSeqFormat.read(JsonParser(rs.string("authentications"))),
    activationKey = rs.stringOpt("activationKey"),
    resetPasswordKey = rs.stringOpt("resetPasswordKey")
  )

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
  date: LocalDateTime
) {
  def crypt(): Authentication = copy(
    token = BCrypt.hashpw(token, BCrypt.gensalt())
  )
}

object Authentication {
  import JsonProtocol._

  implicit val authenticationFormat: JsonFormat[Authentication] = jsonFormat2(Authentication.apply)
  implicit val authenticationSeqFormat: JsonFormat[Seq[Authentication]] = seqJsonFormat(authenticationFormat)
}

case class Profile(
  email: String,
  firstName: String,
  lastName: String,
  address: String,
  zipCode: String,
  city: String,
  phones: Seq[Phone]
)

object Profile {
  import JsonProtocol._

  def apply(rs: WrappedResultSet): Profile = new Profile(
    email = rs.stringOpt("email").getOrElse(""),
    firstName = rs.stringOpt("first_name").getOrElse(""),
    lastName = rs.stringOpt("last_name").getOrElse(""),
    address = rs.stringOpt("address").getOrElse(""),
    zipCode = rs.stringOpt("zip_code").getOrElse(""),
    city = rs.stringOpt("city").getOrElse(""),
    phones = rs.stringOpt("phones").map(phones => Phone.phoneSeqFormat.read(JsonParser(phones))).getOrElse(Seq.empty)
  )

  implicit val profileFormat: RootJsonFormat[Profile] = jsonFormat7(Profile.apply)
}

case class Phone(
  name: String,
  number: String
)

object Phone {
  import JsonProtocol._

  implicit val phoneFormat: JsonFormat[Phone] = jsonFormat2(Phone.apply)
  implicit val phoneSeqFormat: JsonFormat[Seq[Phone]] = seqJsonFormat(phoneFormat)
}

case class UserView(
  id: String,
  email: String,
  firstName: String,
  lastName: String
)

object UserView {
  import JsonProtocol._

  def apply(rs: WrappedResultSet): UserView = new UserView(
    id = rs.stringOpt("id").getOrElse(""),
    email = rs.stringOpt("email").getOrElse(""),
    firstName = rs.stringOpt("first_name").getOrElse(""),
    lastName = rs.stringOpt("last_name").getOrElse("")
  )

  implicit val userViewFormat: JsonFormat[UserView] = jsonFormat4(UserView.apply)
}