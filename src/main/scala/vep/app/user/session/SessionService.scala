package vep.app.user.session

import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt
import scalikejdbc._
import vep.Configuration
import vep.app.user.{Authentication, User, UserMessages, UserService}
import vep.framework.database.DatabaseContainer
import vep.framework.utils.StringUtils
import vep.framework.validation.{Invalid, Valid, Validation}

class SessionService(
  userService: UserService,
  val configuration: Configuration
) extends DatabaseContainer {
  def login(userLogin: UserLogin): Validation[Authentication] = withCommandTransaction { implicit session =>
    userService.findByEmail(userLogin.email) match {
      case Some(user) =>
        verifyAuthentication(user, userLogin)
          .map(user => createAuthentication(user.email))
      case _ =>
        Invalid(UserMessages.invalidLogin)
    }
  }

  private def verifyAuthentication(user: User, userLogin: UserLogin): Validation[UserLogin] = {
    Valid(userLogin)
      .verify(BCrypt.checkpw(userLogin.password, user.password), UserMessages.invalidLogin)
      .verify(user.activationKey.isEmpty, UserMessages.inactive)
  }

  private def createAuthentication(email: String)(implicit session: DBSession): Authentication = {
    val authentication = Authentication(
      token = StringUtils.randomString(),
      date = DateTime.now()
    )
    insertAuthentication(email, authentication)
    authentication
  }

  private def insertAuthentication(email: String, authentication: Authentication)(implicit session: DBSession): Unit = {
    userService.findByEmail(email).foreach { user =>
      val updatedAuthentications = user.authentications :+ authentication.crypt()
      val jsonAuthentications = Authentication.authenticationSeqFormat.write(updatedAuthentications).compactPrint
      sql"""
        UPDATE users
        SET    authentications = $jsonAuthentications
        WHERE  email = $email
      """
        .executeUpdate()
        .apply()
    }
  }
}
