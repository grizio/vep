package vep.app.user

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.user.registration.RegistrationIntegrationModule

trait UserIntegrationModule
  extends RegistrationIntegrationModule {
  def configuration: Configuration

  lazy val userService = new UserService(
    configuration
  )

  lazy val userRoute: Route = registrationRouter.route
}
