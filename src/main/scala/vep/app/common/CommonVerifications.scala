package vep.app.common

import vep.framework.utils.StringUtils
import vep.framework.validation.{Valid, Validation}

import scala.util.matching.Regex

class CommonVerifications {
  private val patternValidatingEmail: Regex = """^([a-zA-Z0-9.!#$%&’'*+/=?^_`{|}~-]+)@([a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*)$""".r
  private val scoreMultiplicationByCharacter = 1
  private val scoreAdditionWhenUppercaseLetter = 10
  private val scoreAdditionWhenLowercaseLetter = 10
  private val scoreAdditionWhenNumber = 10
  private val scoreAdditionWhenSpecialCharacter = 20
  private val minimumScoreRequired = 400

  def verifyEmail(email: String): Validation[String] = {
    Valid(email)
      .filter(patternValidatingEmail.findFirstMatchIn(_).isDefined, CommonMessages.invalidEmail)
  }

  def verifyPassword(password: String): Validation[String] = {
    Valid(password)
      .filter(computeSecurityScore(_) >= minimumScoreRequired, CommonMessages.invalidPassword)
  }

  private def computeSecurityScore(value: String): Long = {
    var total = 0
    if (StringUtils.containsUppercaseLetter(value)) total += scoreAdditionWhenUppercaseLetter
    if (StringUtils.containsLowercaseLetter(value)) total += scoreAdditionWhenLowercaseLetter
    if (StringUtils.containsNumber(value)) total += scoreAdditionWhenNumber
    if (StringUtils.containsSpecialCharacter(value)) total += scoreAdditionWhenSpecialCharacter
    total *= value.length * scoreMultiplicationByCharacter
    total
  }

  def verifyNonEmpty(value: String): Validation[String] = {
    Valid(value)
      .filter(_.nonEmpty, CommonMessages.errorIsEmpty)
  }

  def verifyNonBlank(value: String): Validation[String] = {
    Valid(value.trim)
      .filter(_.nonEmpty, CommonMessages.errorIsBlank)
  }

  def verifyNonEmptySeq[A](value: Seq[A]): Validation[Seq[A]] = {
    Valid(value)
      .filter(_.nonEmpty, CommonMessages.errorIsSeqEmpty)
  }

  def verifyIsPositive(value: Int): Validation[Int] = {
    Valid(value)
      .filter(_ > 0, CommonMessages.isNotPositive)
  }

  def verifyEquals[A](value: A, expected: A): Validation[A] = {
    Valid(value)
      .filter(_ == expected, CommonMessages.isDifferent(value, expected))
  }
}
