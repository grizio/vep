package vep.app.user.registration

import org.mindrot.jbcrypt.BCrypt
import scalikejdbc._
import spray.json.JsonFormat
import vep.app.user.{Authentication, User, UserMessages, UserService}
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Invalid, Valid, Validation}

class RegistrationService(
  userService: UserService
) extends DatabaseContainer {
  def create(user: User): Validation[User] = withCommandTransaction { implicit session =>
    userService.findByEmail(user.email) match {
      case Some(_) => Invalid(UserMessages.userExists(user.email))
      case None => Valid(insert(user))
    }
  }

  private def insert(user: User)(implicit session: DBSession): User = {
    val insertedUser: User = user.copy(
      activationKey = user.activationKey.map(BCrypt.hashpw(_, BCrypt.gensalt())),
      password = BCrypt.hashpw(user.password, BCrypt.gensalt())
    )
    val authentications = implicitly[JsonFormat[Seq[Authentication]]].write(insertedUser.authentications).compactPrint
    sql"""
      INSERT INTO users(id, email, password, role, authentications, activationkey)
      VALUES (${insertedUser.id}, ${insertedUser.email}, ${insertedUser.password}, ${insertedUser.role.toString}, $authentications, ${insertedUser.activationKey})
    """
      .execute()
      .apply()
    user
  }

  def activate(email: String, activationKey: String): Validation[Unit] = withCommandTransaction { implicit session =>
    userService.findByEmail(email) match {
      case Some(user) =>
        user.activationKey match {
          case Some(userActivationKey) =>
            if (BCrypt.checkpw(activationKey, userActivationKey)) {
              removeActivationKey(email)
              Valid()
            } else {
              Invalid(UserMessages.invalidActivationKey)
            }
          case None =>
            Valid()
        }
      case None =>
        Invalid(UserMessages.unknown)
    }
  }

  private def removeActivationKey(email: String)(implicit session: DBSession): Unit = {
    sql"""
      UPDATE users
      SET activationKey = NULL
      where email = ${email}
    """
      .executeUpdate()
      .apply()
  }
}
