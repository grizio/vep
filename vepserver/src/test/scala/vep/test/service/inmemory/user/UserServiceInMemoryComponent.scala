package vep.test.service.inmemory.user

import vep.exception.FieldErrorException
import vep.model.common.ErrorCodes
import vep.model.user.{User, UserRegistration}
import vep.service.user.UserServiceComponent
import vep.utils.StringUtils

trait UserServiceInMemoryComponent extends UserServiceComponent {
  // foreach call, we create a new object
  // It will permit to "clean database" for each test.
  override def userService: UserService = new UserServiceInMemory

  class UserServiceInMemory extends UserService {
    private var users = Seq[User](
      User(1, "aui@aui.com", "abc", "def", "ghi", "jkl", None),
      User(2, "cts@cts.com", "zyx", "wvu", "tsr", "qpo", Some("nml"))
    )

    override def register(userRegistration: UserRegistration): Unit = {
      if (users.exists{ u => u.email == userRegistration.email }) {
        throw new FieldErrorException("email", ErrorCodes.usedEmail, "")
      } else {
        val salt = StringUtils.generateSalt(10)
        val p = StringUtils.crypt(userRegistration.password, salt)
        users = users.+:(User(users.maxBy(u => u.uid).uid + 1, userRegistration.email, p, salt, userRegistration.firstName, userRegistration.lastName, userRegistration.city))
      }
    }
  }
}
