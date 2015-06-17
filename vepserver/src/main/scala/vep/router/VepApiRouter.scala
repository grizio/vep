package vep.router

import spray.routing._
import vep.controller.VepControllersComponent
import vep.router.api.{CmsRouter, UserRouter}
import vep.service.VepServicesComponent

/**
 * This trait defines the route for vep API in terms of all routes grouped here.
 */
trait VepApiRouter
  extends VepRouter
  with VepServicesComponent
  with UserRouter
  with CmsRouter
  with VepControllersComponent {

  override lazy val route: Route = cors { userRoute ~ cmsRoute }
}
