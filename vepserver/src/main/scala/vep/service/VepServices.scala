package vep.service

import vep.AnormClient
import vep.service.cms.{PageServiceComponent, PageServiceProductionComponent}
import vep.service.company.{CompanyServiceProductionComponent, CompanyServiceComponent}
import vep.service.show.{ShowServiceProductionComponent, ShowServiceComponent}
import vep.service.theater.{TheaterServiceComponent, TheaterServiceProductionComponent}
import vep.service.user.{UserServiceComponent, UserServiceProductionComponent}

/**
 * This class defines all services of the vep application
 * (Cake pattern)
 */
trait VepServicesComponent
  extends UserServiceComponent
  with PageServiceComponent
  with TheaterServiceComponent
  with CompanyServiceComponent
  with ShowServiceComponent

trait VepServicesProductionComponent
  extends VepServicesComponent
  with UserServiceProductionComponent
  with PageServiceProductionComponent
  with TheaterServiceProductionComponent
  with CompanyServiceProductionComponent
  with ShowServiceProductionComponent
  with AnormClient