package vep.controller

import vep.exception.FieldErrorException
import vep.model.cms.{PageItem, Page, PageForm, PageFormBody}
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
     * Returns the whole list of pages.
     */
    def list(): ResultSuccessEntity[Seq[PageItem]]
  }
}

trait PageControllerProductionComponent extends PageControllerComponent {
  self: VepServicesComponent =>

  override val pageController: PageController = new PageControllerProduction

  class PageControllerProduction extends PageController {
    override def create(pageFormCanonical: PageForm): Either[ResultErrors, ResultSuccess] = {
      if (pageFormCanonical.verify) {
        try {
          pageService.create(pageFormCanonical)
          Right(ResultSuccess)
        } catch {
          case e: FieldErrorException => Left(e.toResultErrors)
        }
      } else {
        Left(pageFormCanonical.toResult.asInstanceOf[ResultErrors])
      }
    }

    override def list(): ResultSuccessEntity[Seq[PageItem]] = {
      ResultSuccessEntity(pageService.findAll())
    }
  }
}
