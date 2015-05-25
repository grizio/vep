package vep.service.user

import anorm.SqlParser._
import anorm._
import spray.http.DateTime
import spray.routing.authentication.UserPass
import vep.AnormClient
import vep.exception.FieldErrorException
import vep.model.common.ErrorCodes
import vep.model.user.{User, UserLogin, UserRegistration}
import vep.service.AnormImplicits
import vep.utils.{DB, StringUtils}

trait UserServiceComponent {
  def userService: UserService

  trait UserService {
    def register(userRegistration: UserRegistration): Unit

    def login(userLogin: UserLogin): Option[User]

    def authenticate(userPasse: UserPass): Option[User]
  }

}

trait UserServiceProductionComponent extends UserServiceComponent {
  self: AnormClient =>
  override lazy val userService = new UserServiceProduction

  class UserServiceProduction extends UserService with AnormImplicits {
    lazy val userParser =
      int("uid") ~
        str("email") ~
        str("password") ~
        str("salt") ~
        str("firstName") ~
        str("lastName") ~
        get[Option[String]]("city") ~
        get[Option[String]]("keyLogin") ~
        get[Option[DateTime]]("expiration") map {
        case uid ~ email ~ password ~ salt ~ firsName ~ lastName ~ city ~ keyLogin ~ expiration =>
          User(uid, email, password, salt, firsName, lastName, city, keyLogin, expiration)
      }

    override def register(userRegistration: UserRegistration): Unit = DB.withTransaction { implicit c =>
      val salt = StringUtils.generateSalt()
      val password = StringUtils.crypt(userRegistration.password, salt)

      // The use of SELECT FOR UPDATE provides a way to block other transactions
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

    override def login(userLogin: UserLogin): Option[User] = DB.withTransaction { implicit c =>
      val userOpt = SQL("SELECT * FROM users WHERE email = {email}")
        .on("email" -> userLogin.email)
        .as(userParser.singleOpt)

      userOpt flatMap { user =>
        if (StringUtils.crypt(userLogin.password, user.salt) == user.password) {
          val keylogin = StringUtils.generateSalt()
          val expiration = DateTime.now + 86400000 // 24h = 86,400,000ms
          SQL("UPDATE users SET keylogin = {keylogin}, expiration = {expiration} WHERE email = {email}")
            .on("keylogin" -> keylogin)
            .on("expiration" -> expiration.toIsoDateTimeString)
            .on("email" -> user.email)
            .executeUpdate()
          Some(user.copy(keyLogin = Some(keylogin), expiration = Some(expiration)))
        } else {
          None
        }
      }
    }

    override def authenticate(userPass: UserPass): Option[User] = DB.withTransaction { implicit c =>
      val userOpt = SQL("SELECT * FROM users WHERE email = {email} AND keylogin = {keylogin} AND expiration < {expiration}")
        .on("uid" -> userPass.user)
        .on("keylogin" -> userPass.pass)
        .on("expiration" -> DateTime.now.toIsoDateString)
        .as(userParser.singleOpt)

      userOpt foreach { u =>
        SQL("UPDATE users SET expiration = {expiration} WHERE email = {email}")
          .on("email" -> u.email)
          .on("expiration" -> DateTime.now.toIsoDateTimeString)
          .executeUpdate()
      }

      userOpt
    }
  }

}