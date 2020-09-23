package vep.app.user.session

import java.time.LocalDateTime

import org.mindrot.jbcrypt.BCrypt
import scalikejdbc._
import vep.app.user.{Authentication, User, UserMessages, UserService}
import vep.framework.database.DatabaseContainer
import vep.framework.utils.StringUtils
import vep.framework.validation.{Invalid, Valid, Validation}

class SessionService(
  userService: UserService,
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
      date = LocalDateTime.now()
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

  def createResetPasswordKey(email: String): Validation[String] = withCommandTransaction { implicit session =>
    userService.findByEmail(email).map { _ =>
      val resetPasswordKey = StringUtils.randomString()
      val cryptedPasswordKey = BCrypt.hashpw(resetPasswordKey, BCrypt.gensalt())
      updateResetPasswordKey(email, cryptedPasswordKey)
      Valid(resetPasswordKey)
    }.getOrElse(Invalid(UserMessages.unknown))
  }

  private def updateResetPasswordKey(email: String, resetPasswordKey: String)(implicit session: DBSession): Unit = {
    sql"""
      UPDATE users
      SET resetpasswordkey = ${resetPasswordKey}
      WHERE email = ${email}
    """
      .execute()
      .apply()
  }

  def resetPassword(resetPassword: ResetPassword): Validation[Unit] = withCommandTransaction { implicit session =>
    userService.findByEmail(resetPassword.email).map { user =>
      if (BCrypt.checkpw(resetPassword.token, user.resetPasswordKey.getOrElse(""))) {
        val cryptedPassword = BCrypt.hashpw(resetPassword.password, BCrypt.gensalt())
        updatePassword(resetPassword.email, cryptedPassword)
        Valid()
      } else {
        Invalid(UserMessages.invalidResetPasswordKey)
      }
    }.getOrElse(Invalid(UserMessages.unknown))
  }

  private def updatePassword(email: String, password: String)(implicit session: DBSession): Unit = {
    sql"""
      UPDATE users
      SET resetpasswordkey = null,
          password = ${password}
      WHERE email = ${email}
    """
      .execute()
      .apply()
  }
}
