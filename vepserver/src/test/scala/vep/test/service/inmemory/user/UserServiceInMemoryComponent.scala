package vep.test.service.inmemory.user

import spray.http.DateTime
import spray.routing.authentication.UserPass
import vep.exception.FieldErrorException
import vep.model.common.ErrorCodes
import vep.model.user.{User, UserLogin, UserRegistration}
import vep.service.user.UserServiceComponent
import vep.utils.StringUtils

trait UserServiceInMemoryComponent extends UserServiceComponent {
  // foreach call, we create a new object
  // It will permit to "clean database" for each test.
  override def userService: UserService = new UserServiceInMemory

  class UserServiceInMemory extends UserService {
    private var users = Seq[User](
      User(1, "aui@aui.com", StringUtils.crypt("abc", "def"), "def", "ghi", "jkl", None, None, None),
      User(2, "cts@cts.com", StringUtils.crypt("zyx", "wvu"), "wvu", "tsr", "qpo", Some("nml"), Some("a"), Some(DateTime(2000, 1, 1)))
    )

    override def register(userRegistration: UserRegistration): Unit = {
      if (users.exists { u => u.email == userRegistration.email }) {
        throw new FieldErrorException("email", ErrorCodes.usedEmail, "")
      } else {
        val salt = StringUtils.generateSalt(10)
        val p = StringUtils.crypt(userRegistration.password, salt)
        users = users.+:(User(users.maxBy(u => u.uid).uid + 1, userRegistration.email, p, salt, userRegistration.firstName, userRegistration.lastName, userRegistration.city, None, None))
      }
    }

    override def login(userLogin: UserLogin): Option[User] = {
      users.find {
        u => userLogin.email == u.email
      } flatMap { u =>
        if (StringUtils.crypt(userLogin.password, u.salt) == u.password) {
          Some(u.copy(keyLogin = Some("a"), expiration = Some(DateTime.apply(2000, 1, 1))))
        } else {
          None
        }
      }
    }

    override def authenticate(userPasse: UserPass): Option[User] = {
      users find {
        u => u.email == userPasse.user && u.keyLogin.getOrElse("") == userPasse.pass
      }
    }
  }

}
