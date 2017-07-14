package vep.app.user.registration

import java.util.UUID

import vep.app.common.CommonVerifications
import vep.app.user.{User, UserRole}
import vep.framework.utils.StringUtils
import vep.framework.validation._

class RegistrationVerifications(
  commonVerification: CommonVerifications
) {
  def verify(user: UserRegistration): Validation[User] = {
    commonVerification.verifyEmail(user.email) ~ commonVerification.verifyPassword(user.password) map {
      case email ~ password => User(
        id = UUID.randomUUID().toString,
        email = email,
        password = password,
        role = UserRole.user,
        authentications = Seq.empty,
        activationKey = Some(StringUtils.randomString()),
        resetPasswordKey = None
      )
    }
  }
}
