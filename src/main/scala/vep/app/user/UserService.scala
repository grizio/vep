package vep.app.user

import scalikejdbc._
import spray.json.JsonParser
import vep.framework.database.DatabaseContainer

class UserService() extends DatabaseContainer {
  import UserService._

  def findByEmail(email: String): Option[User] = withQueryConnection { implicit session =>
    sql"""
      SELECT * FROM users WHERE email = $email
    """
      .map(toUser)
      .headOption()
      .apply()
  }

  def find(id: String): Option[User] = withQueryConnection { implicit session =>
    sql"""
      SELECT * FROM users WHERE id = $id
    """
      .map(toUser)
      .headOption()
      .apply()
  }

  def findView(id: String): Option[UserView] = withQueryConnection { implicit session =>
    sql"""
      SELECT * FROM users WHERE id = $id
    """
      .map(toUserView)
      .headOption()
      .apply()
  }
}

object UserService {
  def toUser(rs: WrappedResultSet): User = new User(
    id = rs.string("id"),
    email = rs.string("email"),
    password = rs.string("password"),
    role = UserRole.deserialize(rs.string("role")),
    authentications = Authentication.authenticationSeqFormat.read(JsonParser(rs.string("authentications"))),
    activationKey = rs.stringOpt("activationKey"),
    resetPasswordKey = rs.stringOpt("resetPasswordKey")
  )

  def toUserView(rs: WrappedResultSet): UserView = new UserView(
    id = rs.stringOpt("id").getOrElse(""),
    email = rs.stringOpt("email").getOrElse(""),
    firstName = rs.stringOpt("first_name").getOrElse(""),
    lastName = rs.stringOpt("last_name").getOrElse("")
  )
}