package vep.app.user

import scalikejdbc._
import vep.Configuration
import vep.framework.database.DatabaseContainer

class UserService(
  val configuration: Configuration
) extends DatabaseContainer {
  def findByEmail(email: String): Option[User] = withQueryConnection { implicit session =>
    sql"""
      SELECT * FROM users WHERE email = $email
    """
      .map(User.apply)
      .headOption()
      .apply()
  }

  def find(id: String): Option[User] = withQueryConnection { implicit session =>
    sql"""
      SELECT * FROM users WHERE id = $id
    """
      .map(User.apply)
      .headOption()
      .apply()
  }

  def findView(id: String): Option[UserView] = withQueryConnection { implicit session =>
    sql"""
      SELECT * FROM users WHERE id = $id
    """
      .map(UserView.apply)
      .headOption()
      .apply()
  }
}