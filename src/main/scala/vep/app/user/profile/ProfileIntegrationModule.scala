package vep.app.user.profile

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService
import vep.framework.mailer.Mailer

import scala.concurrent.ExecutionContext

trait ProfileIntegrationModule {
  def mailer: Mailer
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext

  lazy val profileVerifications = new ProfileVerifications(
    commonVerifications
  )
  lazy val profileService = new ProfileService(
    configuration
  )
  lazy val profileRouter = new ProfileRouter(
    profileVerifications,
    profileService,
    userService,
    executionContext
  )
}
