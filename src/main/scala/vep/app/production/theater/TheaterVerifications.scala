package vep.app.production.theater

import vep.app.common.verifications.CommonVerifications
import vep.framework.validation.Validation

class TheaterVerifications(
  commonVerifications: CommonVerifications
) {

  def verify(theater: Theater, theaterId: String): Validation[Theater] = {
    commonVerifications.verifyEquals(theater.id, theaterId)
      .map { _ => theater }
  }
}
