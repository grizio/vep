package vep.app.user.session

import vep.Configuration
import vep.app.user.UserService
import vep.framework.mailer.Mailer

import scala.concurrent.ExecutionContext

trait SessionIntegrationModule {
  def configuration: Configuration

  def mailer: Mailer

  def userService: UserService

  def executionContext: ExecutionContext

  lazy val sessionService = new SessionService(
    userService
  )
  lazy val sessionRouter = new SessionRouter(
    sessionService,
    sessionMailer,
    userService,
    executionContext
  )
  lazy val sessionMailer = new SessionMailer(
    mailer,
    configuration
  )
}
