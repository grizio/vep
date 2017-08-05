package vep.app.user.adhesion

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait AdhesionIntegrationModule {
  def configuration: Configuration
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
    userService,
    executionContext
  )
}
