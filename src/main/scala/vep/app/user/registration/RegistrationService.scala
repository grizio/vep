package vep.app.user.registration

import org.mindrot.jbcrypt.BCrypt
import scalikejdbc._
import vep.Configuration
import vep.app.user.{Authentication, User, UserMessages, UserService}
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Invalid, Valid, Validation}

class RegistrationService(
  userService: UserService,
  val configuration: Configuration
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
    val authentications = Authentication.authenticationSeqFormat.write(insertedUser.authentications).compactPrint
    sql"""
      INSERT INTO users(id, email, password, role, authentications, activationkey)
      VALUES (${insertedUser.id}, ${insertedUser.email}, ${insertedUser.password}, ${insertedUser.role.toString}, $authentications, ${insertedUser.activationKey})
    """
      .execute()
      .apply()
    user
  }
}
