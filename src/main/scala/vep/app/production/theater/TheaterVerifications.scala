package vep.app.production.theater

import java.util.UUID

import vep.app.common.CommonVerifications
import vep.framework.validation.{Invalid, Valid, Validation, ~}

class TheaterVerifications(
  commonVerifications: CommonVerifications
) {
  def verifyCreation(theaterCreation: TheaterCreation): Validation[Theater] = {
    Validation.all(
      commonVerifications.verifyNonBlank(theaterCreation.name),
      commonVerifications.verifyNonBlank(theaterCreation.address),
      commonVerifications.verifyNonBlank(theaterCreation.content),
      verifySeats(theaterCreation.seats)
    ) map {
      case name ~ address ~ content ~ seats => Theater(
        UUID.randomUUID().toString,
        name, address, content, seats
      )
    }
  }

  def verify(theater: Theater, theaterId: String): Validation[Theater] = {
    Validation.all(
      commonVerifications.verifyEquals(theater.id, theaterId),
      commonVerifications.verifyNonBlank(theater.name),
      commonVerifications.verifyNonBlank(theater.address),
      commonVerifications.verifyNonBlank(theater.content),
      verifySeats(theater.seats)
    ) map { _ => theater }
  }

  private def verifySeats(seats: Seq[Seat]): Validation[Seq[Seat]] = {
    Valid(seats)
      .flatMap(commonVerifications.verifyNonEmptySeq)
      .flatMap { _ =>
        val seatsValidation = Validation.sequence(seats.map(verifySeat))
        if (seatsValidation.isValid) {
          verifySeatUniqueness(seats)
        } else {
          seatsValidation
        }
      }
  }

  private def verifySeat(seat: Seat): Validation[Seat] = {
    lazy val seatIndication = if (seat.c.nonEmpty) s" (siège ${seat.c})" else " (siège non nommé)"

    Validation.all(
      commonVerifications.verifyNonBlank(seat.c).mapError(_.map(_ + seatIndication)),
      commonVerifications.verifyIsPositive(seat.x).mapError(_.map(_ + seatIndication)),
      commonVerifications.verifyIsPositive(seat.y).mapError(_.map(_ + seatIndication)),
      commonVerifications.verifyIsPositive(seat.w).mapError(_.map(_ + seatIndication)),
      commonVerifications.verifyIsPositive(seat.h).mapError(_.map(_ + seatIndication)),
      commonVerifications.verifyNonBlank(seat.t).mapError(_.map(_ + seatIndication))
    ) map { _ => seat }
  }

  private def verifySeatUniqueness(seats: Seq[Seat]): Validation[Seq[Seat]] = {
    val codes = seats.map(_.c)
    val nonUniqueCodes = codes.filter(code => codes.count(_ == code) > 1)
    if (nonUniqueCodes.nonEmpty) {
      Invalid(nonUniqueCodes.distinct.map(TheaterMessage.seatDefinedTwice))
    } else {
      Valid(seats)
    }
  }
}
