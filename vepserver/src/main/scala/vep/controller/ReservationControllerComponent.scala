package vep.controller

import vep.exception.FieldStructuredErrorException
import vep.model.common.{ErrorCodes, ResultError, ResultStructuredErrors, ResultSuccessEntity}
import vep.model.session.{ReservationDetailSeq, ReservationForm, ReservationFormResult, ReservedSeats}
import vep.service.VepServicesComponent

/**
 * This controller defines action about reservations.
 */
trait ReservationControllerComponent {
  def reservationController: ReservationController

  trait ReservationController {
    /**
     * This function register a new reservation if no error.
     * @param reservationForm The reservation to register
     * @return The reservation id and key if it was registered, otherwise the errors.
     */
    def create(reservationForm: ReservationForm): Either[ResultStructuredErrors, ResultSuccessEntity[ReservationFormResult]]

    /**
     * Returns the list of reservation for given session.
     * @param theater The session theater canonical
     * @param session The session canonical
     * @return The list of reservation
     */
    def fromSession(theater: String, session: String): Either[ResultError, ResultSuccessEntity[ReservationDetailSeq]]

    /**
     * Returns the list of reserved places for given session (fixed theater).
     * @param theater The session theater canonical
     * @param session The session canonical
     * @return The list of reserved places
     */
    def reservedPlacesAsPlan(theater: String, session: String): Either[ResultError, ResultSuccessEntity[ReservedSeats]]

    /**
     * Returns the number of reserved places for given session (dynamic theater).
     * @param theater The theater canonical
     * @param session The session canonical
     * @return The number of reserved places
     */
    def reservedPlacesAsNumber(theater: String, session: String): Either[ResultError, ResultSuccessEntity[Int]]
  }

}

trait ReservationControllerProductionComponent extends ReservationControllerComponent {
  self: VepServicesComponent =>

  override lazy val reservationController = new ReservationControllerProduction()

  class ReservationControllerProduction extends ReservationController {
    override def create(reservationForm: ReservationForm): Either[ResultStructuredErrors, ResultSuccessEntity[ReservationFormResult]] = {
      if (reservationForm.verify) {
        try {
          val result = reservationService.create(reservationForm)
          Right(ResultSuccessEntity(result))
        } catch {
          case e: FieldStructuredErrorException => Left(e.toResult)
        }
      } else {
        Left(reservationForm.toResult.asInstanceOf[ResultStructuredErrors])
      }
    }

    override def fromSession(theaterCanonical: String, sessionCanonical: String): Either[ResultError, ResultSuccessEntity[ReservationDetailSeq]] = {
      type R = Either[ResultError, ResultSuccessEntity[ReservationDetailSeq]]
      lazy val theaterError: R = Left(ResultError(ErrorCodes.undefinedTheater))
      lazy val sessionError: R = Left(ResultError(ErrorCodes.undefinedSession))

      theaterService.findByCanonical(theaterCanonical).fold(theaterError) { theater =>
        sessionService.findId(theaterCanonical, sessionCanonical).fold(sessionError) { sessionID =>
          Right(ResultSuccessEntity(ReservationDetailSeq(reservationService.findFromSession(sessionID))))
        }
      }
    }

    override def reservedPlacesAsPlan(theaterCanonical: String, sessionCanonical: String): Either[ResultError, ResultSuccessEntity[ReservedSeats]] = {
      type R = Either[ResultError, ResultSuccessEntity[ReservedSeats]]
      lazy val theaterError: R = Left(ResultError(ErrorCodes.undefinedTheater))
      lazy val sessionError: R = Left(ResultError(ErrorCodes.undefinedSession))
      lazy val theaterFixedError: R = Left(ResultError(ErrorCodes.notFixedTheater))

      theaterService.findByCanonical(theaterCanonical).fold(theaterError) { theater =>
        if (theater.fixed) {
          sessionService.findId(theaterCanonical, sessionCanonical).fold(sessionError) { sessionID =>
            Right(ResultSuccessEntity(ReservedSeats(reservationService.findReservedPlaces(sessionID))))
          }
        } else {
          theaterFixedError
        }
      }
    }

    override def reservedPlacesAsNumber(theaterCanonical: String, sessionCanonical: String): Either[ResultError, ResultSuccessEntity[Int]] = {
      type R = Either[ResultError, ResultSuccessEntity[Int]]
      lazy val theaterError: R = Left(ResultError(ErrorCodes.undefinedTheater))
      lazy val sessionError: R = Left(ResultError(ErrorCodes.undefinedSession))
      lazy val theaterDynamicError: R = Left(ResultError(ErrorCodes.fixedTheater))

      theaterService.findByCanonical(theaterCanonical).fold(theaterError) { theater =>
        if (theater.fixed) {
          theaterDynamicError
        } else {
          sessionService.findId(theaterCanonical, sessionCanonical).fold(sessionError) { sessionID =>
            Right(ResultSuccessEntity(reservationService.findNumberOfReservedPlaces(sessionID)))
          }
        }
      }
    }
  }
}
