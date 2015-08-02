package vep.test.controller

import vep.controller.{VepControllersComponent, VepControllersProductionComponent}
import vep.test.service.inmemory.VepServicesInMemoryComponent

/**
 * @deprecated Use [[vep.test.controller.VepControllersDBInMemoryComponent]] instead.
 */
@Deprecated
trait VepControllersInMemoryComponent
  extends VepControllersComponent
  with VepControllersProductionComponent
  with VepServicesInMemoryComponent