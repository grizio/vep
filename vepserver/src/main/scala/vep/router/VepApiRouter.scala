package vep.router

import spray.routing._
import vep.controller.VepControllersComponent
import vep.router.api.UserRouter

trait VepApiRouter
  extends VepRouter
  with UserRouter
  with VepControllersComponent {

  override lazy val route: Route = cors { userRoute }
}
