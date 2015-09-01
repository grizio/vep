package vep.controller

import vep.exception.FieldStructuredErrorException
import vep.model.common._
import vep.model.session.{SessionForm, SessionSearch, SessionSearchResponse, SessionUpdateForm}
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

    /**
     * Updates an existing session
     * @param sessionUpdateForm The session to update
     */
    def update(sessionUpdateForm: SessionUpdateForm): Either[ResultStructuredErrors, ResultSuccess]

    /**
     * Searches sessions in terms of given criteria.
     * @param sessionSearch The search criteria
     * @return The list of sessions respecting criteria
     */
    def search(sessionSearch: SessionSearch): Either[ResultErrors, ResultSuccessEntity[SessionSearchResponse]]
  }

}

trait SessionControllerProductionComponent extends SessionControllerComponent {
  self: VepServicesComponent =>

  override lazy val sessionController = new SessionControllerProduction

  class SessionControllerProduction extends SessionController {
    lazy val maxPerPage = 20

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

    override def update(sessionUpdateForm: SessionUpdateForm): Either[ResultStructuredErrors, ResultSuccess] = {
      if (sessionUpdateForm.verify) {
        if (theaterService.exists(sessionUpdateForm.theater)) {
          if (sessionService.exists(sessionUpdateForm.theater, sessionUpdateForm.session)) {
            try {
              sessionService.update(sessionUpdateForm)
              Right(ResultSuccess)
            } catch {
              case e: FieldStructuredErrorException => Left(e.toResult)
            }
          } else {
            Left(ResultStructuredErrors(ErrorItem.build("session" -> ErrorCodes.undefinedSession)))
          }
        } else {
          Left(ResultStructuredErrors(ErrorItem.build("theater" -> ErrorCodes.undefinedTheater)))
        }
      } else {
        Left(sessionUpdateForm.toResult.asInstanceOf[ResultStructuredErrors])
      }
    }

    override def search(sessionSearch: SessionSearch): Either[ResultErrors, ResultSuccessEntity[SessionSearchResponse]] = {
      if (sessionSearch.verify) {
        val result = sessionSearchService.search(sessionSearch, maxPerPage)
        val count = sessionSearchService.count(sessionSearch)
        Right(ResultSuccessEntity(SessionSearchResponse(result, Math.ceil(count.toDouble / maxPerPage.toDouble).toInt)))
      } else {
        Left(sessionSearch.toResult.asInstanceOf[ResultErrors])
      }
    }
  }
}