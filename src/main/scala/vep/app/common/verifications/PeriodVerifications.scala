package vep.app.common.verifications

import org.joda.time.DateTime
import vep.app.common.CommonMessages
import vep.app.common.types.Period
import vep.framework.validation.{Invalid, Valid, Validation}

trait PeriodVerifications {
  def verifyPeriod(period: Period): Validation[Period] = {
    if (period.start.isAfter(period.end)) {
      Valid(Period(start = period.end, end = period.start))
    } else {
      Valid(period)
    }
  }

  def verifyIncluded(period: Period, reference: Period): Validation[Period] = {
    val startAfterOrEqualsReference = period.start.isAfter(reference.start) || period.start.isEqual(reference.start)
    val endBeforeOrEqualsReference = period.end.isBefore(reference.end) || period.end.isEqual(reference.end)
    if (startAfterOrEqualsReference && endBeforeOrEqualsReference) {
      Valid(period)
    } else {
      Invalid(CommonMessages.periodNotIncluded)
    }
  }

  def verifyDateIncluded(date: DateTime, reference: Period): Validation[DateTime] = {
    val isAfterOrEqualsStart = date.isAfter(new DateTime(reference.start)) || date.isEqual(new DateTime(reference.start))
    val isBeforeOrEqualsEnd = date.isBefore(new DateTime(reference.end)) || date.isEqual(new DateTime(reference.end))
    if (isAfterOrEqualsStart && isBeforeOrEqualsEnd) {
      Valid(date)
    } else {
      Invalid(CommonMessages.dateNotIncluded)
    }
  }
}
