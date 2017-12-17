package vep.app.production.company.show

import vep.app.common.verifications.CommonVerifications
import vep.framework.validation.Validation

class ShowVerifications(
  commonVerifications: CommonVerifications
) {

  def verify(show: Show, showId: String): Validation[Show] = {
    commonVerifications.verifyEquals(show.id, showId)
      .map { _ => show }
  }
}
