package vep.controller

import vep.exception.FieldStructuredErrorException
import vep.model.common.{ResultStructuredErrors, ResultSuccessEntity}
import vep.model.session.SessionForm
import vep.service.VepServicesComponent

/**
 * This controller defines action querying or updating sessions.
 */
trait SessionControllerComponent {
  def sessionController: SessionController

  trait SessionController {
    /**
     * Creates a new session and return the generated canonical for this session.
     * @param sessionForm The session to create
     * @return The session canonical
     */
    def create(sessionForm: SessionForm): Either[ResultStructuredErrors, ResultSuccessEntity[String]]
  }

}

trait SessionControllerProductionComponent extends SessionControllerComponent {
  self: VepServicesComponent =>

  override lazy val sessionController = new SessionControllerProduction

  class SessionControllerProduction extends SessionController {
    override def create(sessionForm: SessionForm): Either[ResultStructuredErrors, ResultSuccessEntity[String]] = {
      if (sessionForm.verify) {
        try {
          val canonical = sessionService.create(sessionForm)
          Right(ResultSuccessEntity(canonical))
        } catch {
          case e: FieldStructuredErrorException => Left(e.toResult)
        }
      } else {
        Left(sessionForm.toResult.asInstanceOf[ResultStructuredErrors])
      }
    }
  }

}