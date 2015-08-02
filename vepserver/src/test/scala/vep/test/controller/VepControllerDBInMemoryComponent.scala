package vep.test.controller

import vep.controller.{VepControllersComponent, VepControllersProductionComponent}
import vep.test.service.inmemory.VepServicesDBInMemoryComponent

trait VepControllersDBInMemoryComponent
  extends VepControllersComponent
  with VepControllersProductionComponent
  with VepServicesDBInMemoryComponent
