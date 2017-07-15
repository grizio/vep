package vep.app.common.contact

import vep.app.common.CommonVerifications
import vep.framework.validation.{Validation, ~}

class ContactVerifications(
  commonVerifications: CommonVerifications
) {
  def verify(contact: Contact): Validation[Contact] = {
    (
      commonVerifications.verifyEmail(contact.email)
        ~ commonVerifications.verifyNonBlank(contact.name)
        ~ commonVerifications.verifyNonBlank(contact.title)
        ~ commonVerifications.verifyNonBlank(contact.content)
      ) map { _ => contact }
  }
}
