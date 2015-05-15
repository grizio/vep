package vep.controller

import vep.service.VepServicesComponent

trait VepControllersComponent
  extends UserControllerComponent

trait VepControllersProductionComponent
  extends VepControllersComponent
  with UserControllerProductionComponent
  with VepServicesComponent