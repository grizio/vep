package vep.app.user.adhesion

import vep.app.common.verifications.CommonVerifications
import vep.app.user.User
import vep.framework.validation._
import java.util.UUID

import org.joda.time.DateTime

class AdhesionVerifications(
  commonVerification: CommonVerifications
) {
  def verifyPeriodUpdate(periodAdhesion: PeriodAdhesion, periodId: String): Validation[PeriodAdhesion] = {
    commonVerification.verifyEquals(periodAdhesion.id, periodId)
      .map { _ => periodAdhesion }
  }

  def verifyRequestAdhesion(requestAdhesion: RequestAdhesion, period: PeriodAdhesion, user: User): Validation[Adhesion] = {
    Validation.all(
      commonVerification.verifyEquals(requestAdhesion.period, period.id),
      verifyMembers(requestAdhesion.members, period),
      commonVerification.verifyDateIncluded(DateTime.now(), period.registrationPeriod)
    ) map {
      case _ ~ members ~ _ =>
        Adhesion(
          id = UUID.randomUUID().toString,
          user = user.id,
          accepted = false,
          members: Seq[AdhesionMember]
        )
    }
  }

  private def verifyMembers(members: Seq[AdhesionMember], period: PeriodAdhesion): Validation[Seq[AdhesionMember]] = {
    commonVerification.verifyNonEmptySeq(members)
      .flatMap(_ => Validation.sequence(members.map(verifyMember(_, period))))
  }

  private def verifyMember(member: AdhesionMember, period: PeriodAdhesion): Validation[AdhesionMember] = {
    Validation.all(
      commonVerification.verifyIsBeforeNow(member.birthday),
      commonVerification.verifyExistsIn(member.activity, period.activities)
    ) map { _ => member }
  }
}
