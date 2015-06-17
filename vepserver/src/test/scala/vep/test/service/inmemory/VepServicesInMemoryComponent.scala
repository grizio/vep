package vep.test.service.inmemory

import vep.service.VepServicesComponent
import vep.test.service.inmemory.cms.PageServiceInMemoryComponent
import vep.test.service.inmemory.user.UserServiceInMemoryComponent

trait VepServicesInMemoryComponent
  extends VepServicesComponent
  with UserServiceInMemoryComponent
  with PageServiceInMemoryComponent
{
  def overrideServices = true
}
