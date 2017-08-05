package vep.app.user.adhesion

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService
import vep.framework.mailer.Mailer

import scala.concurrent.ExecutionContext

trait AdhesionIntegrationModule {
  def configuration: Configuration
  def mailer: Mailer
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext

  lazy val adhesionVerifications = new AdhesionVerifications(
    commonVerifications
  )
  lazy val adhesionService = new AdhesionService(
    configuration,
    userService
  )
  lazy val adhesionRouter = new AdhesionRouter(
    adhesionVerifications,
    adhesionService,
    adhesionMailer,
    userService,
    executionContext
  )
  lazy val adhesionMailer = new AdhesionMailer(mailer, configuration)
}
