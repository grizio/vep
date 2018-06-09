package vep.app.user.profile

import org.mindrot.jbcrypt.BCrypt
import vep.app.common.verifications.CommonVerifications
import vep.app.user.{Phone, Profile, User, UserMessages}
import vep.framework.validation._

class ProfileVerifications(
  commonVerification: CommonVerifications
) {
  def verify(profile: Profile, user: User): Validation[Profile] = {
    Validation.all(
      commonVerification.verifyEquals(profile.email, user.email),
      commonVerification.verifyNonBlank(profile.firstName),
      commonVerification.verifyNonBlank(profile.lastName),
      commonVerification.verifyNonBlank(profile.address),
      commonVerification.verifyNonBlank(profile.zipCode),
      commonVerification.verifyNonBlank(profile.city),
      verifyPhones(profile.phones)
    ) map { _ => profile }
  }

  private def verifyPhones(phones: Seq[Phone]): Validation[Seq[Phone]] = {
    commonVerification.verifyNonEmptySeq(phones)
      .flatMap { _ => Validation.sequence(phones.map(verifyPhone)) }
  }

  private def verifyPhone(phone: Phone): Validation[Phone] = {
    Validation.all(
      commonVerification.verifyNonBlank(phone.name),
      commonVerification.verifyNonBlank(phone.number)
    ) map { _ => phone }
  }

  def verifyDeletion(accountDeletion: AccountDeletion, user: User): Validation[AccountDeletion] = {
    Validation.all(
      commonVerification.verifyEquals(accountDeletion.email, user.email),
      Valid(accountDeletion.password).filter(BCrypt.checkpw(_, user.password), UserMessages.invalidPassword)
    ) map { _ => accountDeletion }
  }
}
