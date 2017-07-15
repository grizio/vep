package vep.app.common

import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.common.contact.ContactIntegrationModule
import vep.app.user.UserService

trait CommonIntegrationModule
  extends ContactIntegrationModule {

  def configuration: Configuration
  def userService: UserService

  lazy val commonVerifications = new CommonVerifications()

  lazy val commonRoute: Route = contactRouter.route
}
