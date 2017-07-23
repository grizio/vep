package vep.app.common.verifications

import vep.app.common.CommonMessages
import vep.framework.utils.StringUtils
import vep.framework.validation.{Valid, Validation}

trait PasswordVerification {
  private val scoreMultiplicationByCharacter = 1
  private val scoreAdditionWhenUppercaseLetter = 10
  private val scoreAdditionWhenLowercaseLetter = 10
  private val scoreAdditionWhenNumber = 10
  private val scoreAdditionWhenSpecialCharacter = 20
  private val minimumScoreRequired = 400

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
}
