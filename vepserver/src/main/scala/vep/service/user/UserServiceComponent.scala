package vep.service.user

import anorm.SqlParser._
import anorm._
import vep.AnormClient
import vep.exception.FieldErrorException
import vep.model.common.ErrorCodes
import vep.model.user.UserRegistration
import vep.utils.{DB, StringUtils}

trait UserServiceComponent {
  val userService: UserService

  trait UserService {
    def register(userRegistration: UserRegistration): Unit
  }

}

trait UserServiceProductionComponent extends UserServiceComponent {
  self: AnormClient =>
  override lazy val userService = new UserServiceProduction

  class UserServiceProduction extends UserService {
    override def register(userRegistration: UserRegistration): Unit = DB.withTransaction { implicit c =>
      val salt = StringUtils.generateSalt()
      val password = StringUtils.crypt(userRegistration.password, salt)

      // The use of SELECT FOR UPDATE provide a way to block other transactions
      // and to not throw any exception for because of email duplication.
      val nEmail = SQL("SELECT count(*) FROM users WHERE email = {email} FOR UPDATE")
        .on("email" -> userRegistration.email)
        .as(scalar[Long].single)

      if (nEmail > 0) {
        throw new FieldErrorException("email", ErrorCodes.usedEmail, "The email address is already used.")
      } else {
        SQL("INSERT INTO users(email, firstName, lastName, password, salt, city) VALUES({email}, {firstName}, {lastName}, {password}, {salt}, {city})")
          .on("email" -> userRegistration.email)
          .on("firstName" -> userRegistration.firstName)
          .on("lastName" -> userRegistration.lastName)
          .on("password" -> password)
          .on("salt" -> salt)
          .on("city" -> userRegistration.city)
          .executeInsert()
      }
    }
  }

}