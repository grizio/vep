package vep.app.common.page

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService
import vep.framework.mailer.Mailer

import scala.concurrent.ExecutionContext

trait PageIntegrationModule {
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext

  lazy val pageServices = new PageService(
    configuration
  )
  lazy val pageVerifications = new PageVerifications(
    commonVerifications,
    pageServices
  )
  lazy val pageRouter = new PageRouter(
    pageVerifications,
    pageServices,
    userService,
    executionContext
  )
}
