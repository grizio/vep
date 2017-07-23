package vep.app.common.verifications

import vep.app.common.CommonMessages
import vep.framework.validation.{Valid, Validation}

import scala.util.matching.Regex

trait StringVerifications {
  private val patternValidatingEmail: Regex = """^([a-zA-Z0-9.!#$%&’'*+/=?^_`{|}~-]+)@([a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*)$""".r

  def verifyEmail(email: String): Validation[String] = {
    Valid(email)
      .filter(patternValidatingEmail.findFirstMatchIn(_).isDefined, CommonMessages.invalidEmail)
  }

  def verifyNonEmpty(value: String): Validation[String] = {
    Valid(value)
      .filter(_.nonEmpty, CommonMessages.errorIsEmpty)
  }

  def verifyNonBlank(value: String): Validation[String] = {
    Valid(value.trim)
      .filter(_.nonEmpty, CommonMessages.errorIsBlank)
  }
}
