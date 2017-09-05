package vep.app.common.page

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait PageIntegrationModule {
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext
  def configuration: Configuration

  lazy val pageServices = new PageService()
  lazy val pageVerifications = new PageVerifications(
    commonVerifications,
    pageServices
  )
  lazy val pageRouter = new PageRouter(
    pageVerifications,
    pageServices,
    configuration,
    userService,
    executionContext
  )
}
