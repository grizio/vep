package vep.test.service.inmemory

import vep.service.VepServicesComponent
import vep.test.service.inmemory.cms.PageServiceInMemoryComponent
import vep.test.service.inmemory.theater.TheaterServiceInMemoryComponent
import vep.test.service.inmemory.user.UserServiceInMemoryComponent

/**
 * @deprecated Use [[vep.test.service.inmemory.VepServicesDBInMemoryComponent]] instead.
 */
@Deprecated
trait VepServicesInMemoryComponent
  extends VepServicesComponent
  with UserServiceInMemoryComponent
  with PageServiceInMemoryComponent
  with TheaterServiceInMemoryComponent
{
  def overrideServices = true

  override def companyService: CompanyService = ???

  override def showService: ShowService = ???

  override def showSearchService: ShowSearchService = ???
}
