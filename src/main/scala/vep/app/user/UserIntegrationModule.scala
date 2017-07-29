package vep.app.user

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.user.profile.ProfileIntegrationModule
import vep.app.user.registration.RegistrationIntegrationModule
import vep.app.user.session.SessionIntegrationModule

trait UserIntegrationModule
  extends RegistrationIntegrationModule
    with SessionIntegrationModule
    with ProfileIntegrationModule {
  def configuration: Configuration

  lazy val userService = new UserService(
    configuration
  )

  lazy val userRoute: Route = registrationRouter.route ~ sessionRouter.route ~ profileRouter.route
}
