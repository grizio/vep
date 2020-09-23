package vep.app.production.reservation

import java.time.LocalDateTime
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
      verifyPrices(reservationCreation, play),
      verifyReservationsNotClosed(play)
    ) map {
      case firstName ~ lastName ~ email ~ seats ~ prices ~ _ => Reservation(
        UUID.randomUUID().toString,
        firstName, lastName, email, reservationCreation.city, reservationCreation.comment, reservationCreation.newsletter,
        seats, prices
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
      .filter(_ => play.reservationEndDate.isAfter(LocalDateTime.now()), ReservationMessages.closedReservation)
  }

  private def verifyPrices(creation: ReservationCreation, play: Play): Validation[Seq[ReservationPrice]] = {
    Valid(creation.prices)
      .filter(_.forall(price => play.prices.exists(_.name == price.price)), ReservationMessages.unknownPrice)
      .filter(_.map(_.count).sum == creation.seats.length, ReservationMessages.notSameNumberOfPrices)
  }
}
