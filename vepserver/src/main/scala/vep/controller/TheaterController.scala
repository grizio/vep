package vep.controller

import vep.exception.FieldErrorException
import vep.model.common._
import vep.model.theater.{Theater, TheaterForm}
import vep.service.VepServicesComponent

/**
 * This controller defines actions querying or updating theaters.
 */
trait TheaterControllerComponent {
  val theaterController: TheaterController

  trait TheaterController {
    /**
     * Inserts a theater into database
     * @param theaterForm The theater to insert
     * @return A list of errors if data are invalid or there is a database constraint error or a simple success
     */
    def create(theaterForm: TheaterForm): Either[ResultErrors, ResultSuccess]

    /**
     * Updates an existing theater from database
     * @param theaterForm The theater to update
     * @return A list of errors if data are invalid or there is a database constraint error or a simple success
     */
    def update(theaterForm: TheaterForm): Either[ResultErrors, ResultSuccess]

    /**
     * Returns the whole list of theaters.
     */
    def list(): ResultSuccessEntity[Seq[Theater]]

    /**
     * Checks if a theater is locked.
     * @param canonical The theater canonical
     * @return True if the theater is locked, otherwise false.
     */
    def isLocked(canonical: String): Either[ResultError, ResultSuccessEntity[Boolean]]
  }

}

trait TheaterControllerProductionComponent extends TheaterControllerComponent {
  self: VepServicesComponent =>

  override val theaterController: TheaterController = new TheaterControllerProduction

  class TheaterControllerProduction extends TheaterController {
    override def create(theaterForm: TheaterForm): Either[ResultErrors, ResultSuccess] = {
      if (theaterForm.verify) {
        try {
          theaterService.create(theaterForm)
          Right(ResultSuccess)
        } catch {
          case e: FieldErrorException => Left(e.toResultErrors)
        }
      } else {
        Left(theaterForm.toResult.asInstanceOf[ResultErrors])
      }
    }

    override def update(theaterForm: TheaterForm): Either[ResultErrors, ResultSuccess] = {
      if (theaterService.exists(theaterForm.canonical)) {
        if (theaterForm.verify) {
          try {
            theaterService.update(theaterForm)
            Right(ResultSuccess)
          } catch {
            case e: FieldErrorException => Left(e.toResultErrors)
          }
        } else {
          Left(theaterForm.toResult.asInstanceOf[ResultErrors])
        }
      } else {
        Left(ResultErrors(Map(
          "canonical" -> Seq(ErrorCodes.unknownCanonical)
        )))
      }
    }

    override def list(): ResultSuccessEntity[Seq[Theater]] = {
      ResultSuccessEntity(theaterService.findAll())
    }

    override def isLocked(canonical: String): Either[ResultError, ResultSuccessEntity[Boolean]] = {
      if (theaterService.exists(canonical)) {
        Right(ResultSuccessEntity(theaterService.isLocked(canonical)))
      } else {
        Left(ResultError(ErrorCodes.undefinedTheater))
      }
    }
  }

}
