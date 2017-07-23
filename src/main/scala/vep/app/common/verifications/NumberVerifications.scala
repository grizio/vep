package vep.app.common.verifications

import vep.app.common.CommonMessages
import vep.framework.validation.{Valid, Validation}

trait NumberVerifications {
  def verifyIsPositive[A](value: A)(implicit cmp: Ordering[A], num: Numeric[A]): Validation[A] = {
    Valid(value)
      .filter(number => cmp.gt(number, num.zero), CommonMessages.isNotPositive)
  }
}
