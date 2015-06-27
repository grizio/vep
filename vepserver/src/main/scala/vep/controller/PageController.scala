package vep.controller

import vep.exception.FieldErrorException
import vep.model.cms.{Page, PageForm, PageItem}
import vep.model.common._
import vep.service.VepServicesComponent

/**
 * This controller defines actions querying or updating presentation page.
 */
trait PageControllerComponent {
  val pageController: PageController

  trait PageController {
    /**
     * Inserts a page into database
     * @param pageForm The page to insert
     * @return A list of errors if data are invalid or there is a database constraint error or a simple success
     */
    def create(pageForm: PageForm): Either[ResultErrors, ResultSuccess]

    /**
     * Updates un existing page from database.
     * @param pageForm The page to update
     * @return A list of errors if data are invalid or there is a database constraint error or a simple success.
     */
    def update(pageForm: PageForm): Either[ResultErrors, ResultSuccess]

    /**
     * Returns the whole list of pages.
     */
    def list(): ResultSuccessEntity[Seq[Page]]
  }

}

trait PageControllerProductionComponent extends PageControllerComponent {
  self: VepServicesComponent =>

  override val pageController: PageController = new PageControllerProduction

  class PageControllerProduction extends PageController {
    override def create(pageForm: PageForm): Either[ResultErrors, ResultSuccess] = {
      if (pageForm.verify) {
        try {
          pageService.create(pageForm)
          Right(ResultSuccess)
        } catch {
          case e: FieldErrorException => Left(e.toResultErrors)
        }
      } else {
        Left(pageForm.toResult.asInstanceOf[ResultErrors])
      }
    }

    override def update(pageForm: PageForm): Either[ResultErrors, ResultSuccess] = {
      if (pageService.exists(pageForm.canonical)) {
        if (pageForm.verify) {
          try {
            pageService.update(pageForm)
            Right(ResultSuccess)
          } catch {
            case e: FieldErrorException => Left(e.toResultErrors)
          }
        } else {
          Left(pageForm.toResult.asInstanceOf[ResultErrors])
        }
      } else {
        Left(ResultErrors(Map(
          "canonical" -> Seq(ErrorCodes.unknownCanonical)
        )))
      }
    }

    override def list(): ResultSuccessEntity[Seq[Page]] = {
      ResultSuccessEntity(pageService.findAll())
    }
  }

}
