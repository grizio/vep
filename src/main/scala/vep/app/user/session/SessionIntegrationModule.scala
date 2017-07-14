package vep.app.user.session

import vep.Configuration
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait SessionIntegrationModule {
  def configuration: Configuration

  def userService: UserService

  def executionContext: ExecutionContext

  lazy val sessionService = new SessionService(
    userService,
    configuration
  )
  lazy val sessionRouter = new SessionRouter(
    sessionService,
    userService,
    executionContext
  )
}
