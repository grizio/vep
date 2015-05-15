package vep.test.controller

import vep.controller.{VepControllersComponent, VepControllersProductionComponent}
import vep.test.service.inmemory.VepServicesInMemoryComponent

trait VepControllersInMemoryComponent
  extends VepControllersComponent
  with VepControllersProductionComponent
  with VepServicesInMemoryComponent