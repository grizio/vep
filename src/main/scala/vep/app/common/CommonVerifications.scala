package vep.app.common

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
    if (containsUppercaseLetter(value)) total += scoreAdditionWhenUppercaseLetter
    if (containsLowercaseLetter(value)) total += scoreAdditionWhenLowercaseLetter
    if (containsNumber(value)) total += scoreAdditionWhenNumber
    if (containsSpecialCharacter(value)) total += scoreAdditionWhenSpecialCharacter
    total *= value.length * scoreMultiplicationByCharacter
    total
  }

  private def containsUppercaseLetter(value: String) = value.exists(char => char.isLetter && char.isUpper)

  private def containsLowercaseLetter(value: String) = value.exists(char => char.isLetter && char.isLower)

  private def containsNumber(value: String) = value.exists(char => char.isDigit)

  private def containsSpecialCharacter(value: String) = value.exists(char => !char.isLetterOrDigit)
}
