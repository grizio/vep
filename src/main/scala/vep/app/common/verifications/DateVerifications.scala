package vep.app.common.verifications

import java.time.LocalDateTime

import vep.app.common.CommonMessages
import vep.framework.validation.{Valid, Validation}

trait DateVerifications {
  def verifyIsAfterNow(date: LocalDateTime, message: String = CommonMessages.isNotFuture): Validation[LocalDateTime] = {
    Valid(date)
      .filter(_.isAfter(LocalDateTime.now()), message)
  }

  def verifyIsBeforeNow(date: LocalDateTime, message: String = CommonMessages.isNotPassed): Validation[LocalDateTime] = {
    Valid(date)
      .filter(_.isBefore(LocalDateTime.now()), message)
  }

  def verifyIsBefore(date: LocalDateTime, referenceDate: LocalDateTime, message: String = CommonMessages.isAfter): Validation[LocalDateTime] = {
    Valid(date)
      .filter(_.isBefore(referenceDate), message)
  }

  def verifyIsAfter(date: LocalDateTime, referenceDate: LocalDateTime, message: String = CommonMessages.isAfter): Validation[LocalDateTime] = {
    Valid(date)
      .filter(_.isAfter(referenceDate), message)
  }
}
