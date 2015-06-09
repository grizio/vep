package vep.service.user

import anorm.SqlParser._
import anorm._
import spray.http.DateTime
import spray.routing.authentication.UserPass
import vep.AnormClient
import vep.exception.FieldErrorException
import vep.model.common.ErrorCodes
import vep.model.user.{User, UserForAdmin, UserLogin, UserRegistration}
import vep.service.AnormImplicits
import vep.utils.{DB, StringUtils}

/**
 * Defines services impacting users.
 */
trait UserServiceComponent {
  def userService: UserService

  trait UserService {
    /**
     * Inserts a user into database
     * @param userRegistration The user to insert
     */
    def register(userRegistration: UserRegistration): Unit

    /**
     * Checks the authentication of the user and generate a session key.
     * @param userLogin The user to log in
     * @return The session key or nothing if the authentication is invalid.
     */
    def login(userLogin: UserLogin): Option[User]

    /**
     * Checks if the session is valid (email, session key).
     * @param userPasse The session identification.
     * @return The corresponding user if the identification is valid, otherwise None.
     */
    def authenticate(userPasse: UserPass): Option[User]

    /**
     * Finds a user by its id.
     * @param uid The user id
     * @return The user with this id or nothing.
     */
    def find(uid: Long): Option[User]

    /**
     * Updates the roles of the user
     * @param user The user with new roles.
     */
    def updateRoles(user: User): Unit

    /**
     * Finds all users for administrator.
     */
    def findAllForAdmin(): Seq[UserForAdmin]
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
        get[Option[DateTime]]("expiration") ~
        get[String]("roles") map {
        case uid ~ email ~ password ~ salt ~ firsName ~ lastName ~ city ~ keyLogin ~ expiration ~ roles =>
          User(uid, email, password, salt, firsName, lastName, city, keyLogin, expiration, roles.split(","))
      }

    lazy val userForAdminParser =
      int("uid") ~
        str("email") ~
        str("firstname") ~
        str("lastname") ~
        str("roles") map {
        case uid ~ email ~ firstname ~ lastname ~ roles =>
          UserForAdmin(uid, email, firstname, lastname, roles.split(","))
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
        SQL("INSERT INTO users(email, firstName, lastName, password, salt, city, roles) VALUES({email}, {firstName}, {lastName}, {password}, {salt}, {city}, {roles})")
          .on("email" -> userRegistration.email)
          .on("firstName" -> userRegistration.firstName)
          .on("lastName" -> userRegistration.lastName)
          .on("password" -> password)
          .on("salt" -> salt)
          .on("city" -> userRegistration.city)
          .on("roles" -> "user")
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
      val userOpt = SQL("SELECT * FROM users WHERE email = {email} AND keylogin = {keylogin} AND expiration > {expiration}")
        .on("email" -> userPass.user)
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

    override def find(uid: Long): Option[User] = DB.withConnection { implicit c =>
      SQL("SELECT * FROM users WHERE uid = {uid}")
        .on("uid" -> uid)
        .as(userParser.singleOpt)
    }

    override def updateRoles(user: User): Unit = DB.withTransaction { implicit c =>
      SQL("UPDATE users SET roles = {roles} WHERE uid = {uid}")
        .on("uid" -> user.uid)
        .on("roles" -> user.roles.mkString(","))
        .executeUpdate()
    }

    override def findAllForAdmin(): Seq[UserForAdmin] = DB.withConnection { implicit c =>
      SQL("SELECT uid, email, firstName, lastName, roles FROM users u")
        .as(userForAdminParser *)
    }
  }

}