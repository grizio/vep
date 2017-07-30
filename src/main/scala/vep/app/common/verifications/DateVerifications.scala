package vep.app.common.verifications

import org.joda.time.DateTime
import vep.app.common.CommonMessages
import vep.framework.validation.{Valid, Validation}

trait DateVerifications {
  def verifyIsAfterNow(date: DateTime, message: String = CommonMessages.isNotFuture): Validation[DateTime] = {
    Valid(date)
      .filter(_.isAfterNow, message)
  }

  def verifyIsBeforeNow(date: DateTime, message: String = CommonMessages.isNotPassed): Validation[DateTime] = {
    Valid(date)
      .filter(_.isBeforeNow, message)
  }

  def verifyIsBefore(date: DateTime, referenceDate: DateTime, message: String = CommonMessages.isAfter): Validation[DateTime] = {
    Valid(date)
      .filter(_.isBefore(referenceDate), message)
  }

  def verifyIsAfter(date: DateTime, referenceDate: DateTime, message: String = CommonMessages.isAfter): Validation[DateTime] = {
    Valid(date)
      .filter(_.isAfter(referenceDate), message)
  }
}
