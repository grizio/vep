package vep.app.user.adhesion

import vep.app.common.verifications.CommonVerifications
import vep.app.user.{Phone, Profile, User}
import vep.framework.validation._
import java.util.UUID

class AdhesionVerifications(
  commonVerification: CommonVerifications
) {
  def verifyPeriod(periodAdhesion: PeriodAdhesionCreation): Validation[PeriodAdhesion] = {
    Validation.all(
      commonVerification.verifyPeriod(periodAdhesion.period),
      commonVerification.verifyPeriod(periodAdhesion.registrationPeriod),
      commonVerification.verifyNonEmptySeq(periodAdhesion.activities)
    ) map {
      case period ~ registrationPeriod ~ activities =>
        PeriodAdhesion(
          id = UUID.randomUUID().toString,
          period = period,
          registrationPeriod = registrationPeriod,
          activities = activities
        )
    }
  }
}
