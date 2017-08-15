package vep.app.user.registration

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService
import vep.framework.mailer.Mailer

import scala.concurrent.ExecutionContext

trait RegistrationIntegrationModule {
  def mailer: Mailer
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext

  lazy val registrationMailer = new RegistrationMailer(
    mailer,
    configuration
  )
  lazy val registrationVerifications = new RegistrationVerifications(
    commonVerifications
  )
  lazy val registrationService = new RegistrationService(
    userService
  )
  lazy val registrationRouter = new RegistrationRouter(
    registrationVerifications,
    registrationService,
    registrationMailer,
    userService,
    executionContext
  )
}
