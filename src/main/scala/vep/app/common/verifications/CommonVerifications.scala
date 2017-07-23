package vep.app.common.verifications

import vep.app.common.CommonMessages
import vep.framework.validation.{Invalid, Valid, Validation}

class CommonVerifications
  extends DateVerifications
    with NumberVerifications
    with PasswordVerification
    with SeqVerifications
    with StringVerifications {
  def verifyEquals[A](value: A, expected: A): Validation[A] = {
    Valid(value)
      .filter(_ == expected, CommonMessages.isDifferent(value, expected))
  }

  def verifyIsDefined[A](value: Option[A], message: String = CommonMessages.notFound): Validation[A] = {
    value match {
      case Some(element) => Valid(element)
      case None => Invalid(message)
    }
  }
}
