package vep.app.common.verifications

import vep.app.common.CommonMessages
import vep.framework.validation.{Valid, Validation}

trait SeqVerifications {
  def verifyNonEmptySeq[A](value: Seq[A]): Validation[Seq[A]] = {
    Valid(value)
      .filter(_.nonEmpty, CommonMessages.errorIsSeqEmpty)
  }
}
