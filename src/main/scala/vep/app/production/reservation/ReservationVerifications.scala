package vep.app.production.reservation

import java.util.UUID

import vep.app.common.verifications.CommonVerifications
import vep.app.production.company.show.play.Play
import vep.framework.validation._

class ReservationVerifications(
  commonVerifications: CommonVerifications,
  reservationService: ReservationService
) {
  def verifyCreation(reservationCreation: ReservationCreation, play: Play): Validation[Reservation] = {
    Validation.all(
      commonVerifications.verifyNonEmpty(reservationCreation.firstName),
      commonVerifications.verifyNonEmpty(reservationCreation.lastName),
      commonVerifications.verifyNonEmpty(reservationCreation.email),
      verifySeats(reservationCreation.seats, play),
      verifyReservationsNotClosed(play)
    ) map {
      case firstName ~ lastName ~ email ~ seats ~ _ => Reservation(
        UUID.randomUUID().toString,
        firstName, lastName, email, reservationCreation.city, reservationCreation.comment, seats
      )
    }
  }

  private def verifySeats(seats: Seq[String], play: Play): Validation[Seq[String]] = {
    Validation.all(
      commonVerifications.verifyNonEmptySeq(seats),
      commonVerifications.verifyAllUnique(seats),
      commonVerifications.verifyContainsNoForbiddenElements(seats, reservationService.findReservedSeats(play.id))
    ) map { _ => seats }
  }

  private def verifyReservationsNotClosed(play: Play): Validation[Unit] = {
    Valid()
      .filter(_ => play.reservationEndDate.isAfterNow, ReservationMessages.closedReservation)
  }
}
