package vep.router

import spray.routing._
import vep.controller.VepControllersComponent
import vep.router.api.UserRouter
import vep.service.VepServicesComponent

trait VepApiRouter
  extends VepRouter
  with VepServicesComponent
  with UserRouter
  with VepControllersComponent {

  override lazy val route: Route = cors { userRoute }
}
