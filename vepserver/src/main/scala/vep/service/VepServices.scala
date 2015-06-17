package vep.service

import vep.AnormClient
import vep.service.cms.{PageServiceComponent, PageServiceProductionComponent}
import vep.service.user.{UserServiceComponent, UserServiceProductionComponent}

/**
 * This class defines all services of the vep application
 * (Cake pattern)
 */
trait VepServicesComponent
  extends UserServiceComponent
  with PageServiceComponent

trait VepServicesProductionComponent
  extends VepServicesComponent
  with UserServiceProductionComponent
  with PageServiceProductionComponent
  with AnormClient