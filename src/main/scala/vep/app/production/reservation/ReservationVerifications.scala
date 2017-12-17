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
      verifySeats(reservationCreation.seats, play),
      verifyPrices(reservationCreation, play),
      verifyReservationsNotClosed(play)
    ) map {
      case seats ~ prices ~ _ => Reservation(
        UUID.randomUUID().toString,
        reservationCreation.firstName, reservationCreation.lastName, reservationCreation.email,
        reservationCreation.city, reservationCreation.comment,
        seats, prices
      )
    }
  }

  private def verifySeats(seats: Seq[String], play: Play): Validation[Seq[String]] = {
    commonVerifications.verifyContainsNoForbiddenElements(seats, reservationService.findReservedSeats(play.id))
  }

  private def verifyReservationsNotClosed(play: Play): Validation[LocalDateTime] = {
    commonVerifications.verifyIsAfterNow(play.reservationEndDate, ReservationMessages.closedReservation)
  }

  private def verifyPrices(creation: ReservationCreation, play: Play): Validation[Seq[ReservationPrice]] = {
    Valid(creation.prices)
      .filter(_.forall(price => play.prices.exists(_.name == price.price)), ReservationMessages.unknownPrice)
  }
}
