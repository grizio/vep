package vep.controller

import vep.exception.FieldErrorException
import vep.model.common.{ErrorCodes, ResultSuccessEntity, ResultSuccess, ResultErrors}
import vep.model.company.{Company, CompanyForm}
import vep.model.show.ShowForm
import vep.service.VepServicesComponent

/**
 * This controller defines actions querying or updating theaters.
 */
trait ShowControllerComponent {
  val showController: ShowController

  trait ShowController {
    /**
     * Inserts a show into database
     * @param showForm The show to insert
     * @return A list of errors if data are invalid or there is a database constraint error or a simple success
     */
    def create(showForm: ShowForm): Either[ResultErrors, ResultSuccess]

    /**
     * Updates an existing show from database
     * @param showForm The show to update
     * @return A list of errors if data are invalid or there is a database constraint error or simple success.
     */
    def update(showForm: ShowForm): Either[ResultErrors, ResultSuccess]
  }
}

trait ShowControllerProductionComponent extends ShowControllerComponent {
  self: VepServicesComponent =>

  override val showController: ShowController = new ShowControllerProduction

  class ShowControllerProduction extends ShowController {
    override def create(showForm: ShowForm): Either[ResultErrors, ResultSuccess] = {
      if (showForm.verify) {
        try {
          showService.create(showForm)
          Right(ResultSuccess)
        } catch {
          case e: FieldErrorException => Left(e.toResultErrors)
        }
      } else {
        Left(showForm.toResult.asInstanceOf[ResultErrors])
      }
    }

    override def update(showForm: ShowForm): Either[ResultErrors, ResultSuccess] = {
      if (showForm.verify) {
        try {
          showService.update(showForm)
          Right(ResultSuccess)
        } catch {
          case e: FieldErrorException => Left(e.toResultErrors)
        }
      } else {
        Left(showForm.toResult.asInstanceOf[ResultErrors])
      }
    }
  }
}
