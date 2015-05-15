package vep.service

import vep.AnormClient
import vep.service.user.{UserServiceComponent, UserServiceProductionComponent}

trait VepServicesComponent
  extends UserServiceComponent

trait VepServicesProductionComponent
  extends VepServicesComponent
  with UserServiceProductionComponent
  with AnormClient