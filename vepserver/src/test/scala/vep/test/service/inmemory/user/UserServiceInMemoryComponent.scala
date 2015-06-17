package vep.test.service.inmemory.user

import spray.http.DateTime
import spray.routing.authentication.UserPass
import vep.exception.FieldErrorException
import vep.model.common.{ErrorCodes, Roles}
import vep.model.user.{User, UserForAdmin, UserLogin, UserRegistration}
import vep.service.user.UserServiceComponent
import vep.test.service.inmemory.VepServicesInMemoryComponent
import vep.utils.StringUtils

trait UserServiceInMemoryComponent extends UserServiceComponent {
  self: VepServicesInMemoryComponent =>

  lazy val userServicePermanent = new UserServiceInMemory

  // foreach call, we create a new object if overrideUserService is true.
  // It will permit to "clean database" for each test.
  override def userService: UserService = if (overrideServices) new UserServiceInMemory else userServicePermanent

  class UserServiceInMemory extends UserService {
    private var users = Seq[User](
      User(1, "aui@aui.com", StringUtils.crypt("abc", "def"), "def", "ghi", "jkl", None, None, None),
      User(2, "cts@cts.com", StringUtils.crypt("zyx", "wvu"), "wvu", "tsr", "qpo", Some("nml"), Some("a"), Some(DateTime(2000, 1, 1))),
      User(3, "abc@def.com", StringUtils.crypt("abc", "zyx"), "zyx", "firstname", "lastname", None, Some("abcd"), Some(DateTime.now), Seq("user")),
      User(4, "admin@admin.com", StringUtils.crypt("admin", "admin"), "admin", "admin", "admin", None, Some("admin"), Some(DateTime.now), Roles.acceptedRoles.toList)
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

    override def find(uid: Long): Option[User] = {
      users find {
        u => u.uid == uid
      }
    }

    override def updateRoles(user: User): Unit = {
      users = users map { u =>
        if (u.uid == user.uid) {
          u.copy(roles = user.roles)
        } else {
          u
        }
      }
    }

    override def findAllForAdmin(): Seq[UserForAdmin] = users.map {
      u => UserForAdmin(u.uid, u.email, u.firstName, u.lastName, u.roles)
    }
  }

}
