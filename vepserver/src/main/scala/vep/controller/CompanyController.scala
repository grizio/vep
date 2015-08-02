package vep.controller

import vep.exception.FieldErrorException
import vep.model.common._
import vep.model.company.{Company, CompanyForm}
import vep.model.theater.{Theater, TheaterForm}
import vep.service.VepServicesComponent

/**
 * This controller defines actions querying or updating theaters.
 */
trait CompanyControllerComponent {
  val companyController: CompanyController

  trait CompanyController {
    /**
     * Inserts a company into database
     * @param companyForm The company to insert
     * @return A list of errors if data are invalid or there is a database constraint error or a simple success
     */
    def create(companyForm: CompanyForm): Either[ResultErrors, ResultSuccess]

    /**
     * Updates an existing company from database
     * @param companyForm The company to update
     * @return A list of errors if data are invalid or there is a database constraint error or a simple success
     */
    def update(companyForm: CompanyForm): Either[ResultErrors, ResultSuccess]

    /**
     * Returns the whole list of companies.
     */
    def list(): ResultSuccessEntity[Seq[Company]]
  }
}

trait CompanyControllerProductionComponent extends CompanyControllerComponent {
  self: VepServicesComponent =>

  override val companyController: CompanyController = new CompanyControllerProduction

  class CompanyControllerProduction extends CompanyController {
    override def create(companyForm: CompanyForm): Either[ResultErrors, ResultSuccess] = {
      if (companyForm.verify) {
        try {
          companyService.create(companyForm)
          Right(ResultSuccess)
        } catch {
          case e: FieldErrorException => Left(e.toResultErrors)
        }
      } else {
        Left(companyForm.toResult.asInstanceOf[ResultErrors])
      }
    }

    override def update(companyForm: CompanyForm): Either[ResultErrors, ResultSuccess] = {
      if (companyService.exists(companyForm.canonical)) {
        if (companyForm.verify) {
          try {
            companyService.update(companyForm)
            Right(ResultSuccess)
          } catch {
            case e: FieldErrorException => Left(e.toResultErrors)
          }
        } else {
          Left(companyForm.toResult.asInstanceOf[ResultErrors])
        }
      } else {
        Left(ResultErrors(Map(
          "canonical" -> Seq(ErrorCodes.unknownCanonical)
        )))
      }
    }

    override def list(): ResultSuccessEntity[Seq[Company]] = {
      ResultSuccessEntity(companyService.findAll())
    }
  }

}
