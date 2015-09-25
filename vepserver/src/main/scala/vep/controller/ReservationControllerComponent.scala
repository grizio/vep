package vep.controller

import vep.exception.FieldStructuredErrorException
import vep.model.common.{ResultStructuredErrors, ResultSuccessEntity}
import vep.model.session.{ReservationForm, ReservationFormResult}
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
  }

}
