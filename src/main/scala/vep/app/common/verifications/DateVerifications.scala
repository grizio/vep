package vep.app.common.verifications

import java.time.LocalDateTime

import vep.app.common.CommonMessages
import vep.framework.utils.DateUtils
import vep.framework.validation.{Valid, Validation}

trait DateVerifications {
  def verifyIsAfterNow(date: LocalDateTime, message: String = CommonMessages.isNotFuture): Validation[LocalDateTime] = {
    Valid(date).filter(DateUtils.isAfterNow, message)
  }

  def verifyIsBeforeNow(date: LocalDateTime, message: String = CommonMessages.isNotPassed): Validation[LocalDateTime] = {
    Valid(date).filter(DateUtils.isBeforeNow, message)
  }

  def verifyIsBefore(date: LocalDateTime, referenceDate: LocalDateTime, message: String = CommonMessages.isAfter): Validation[LocalDateTime] = {
    Valid(date).filter(DateUtils.isBefore(_, referenceDate), message)
  }

  def verifyIsAfter(date: LocalDateTime, referenceDate: LocalDateTime, message: String = CommonMessages.isAfter): Validation[LocalDateTime] = {
    Valid(date).filter(DateUtils.isAfter(_, referenceDate), message)
  }
}
