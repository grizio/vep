package vep.controller

import vep.service.VepServicesComponent

/**
 * This trait groups the whole list of application controller definitions.
 * (Cake pattern)
 */
trait VepControllersComponent
  extends UserControllerComponent
  with PageControllerComponent
  with TheaterControllerComponent

/**
 * This trait is the production trait grouping the whole list of production controllers.
 */
trait VepControllersProductionComponent
  extends VepControllersComponent
  with UserControllerProductionComponent
  with VepServicesComponent
  with PageControllerProductionComponent
  with TheaterControllerProductionComponent