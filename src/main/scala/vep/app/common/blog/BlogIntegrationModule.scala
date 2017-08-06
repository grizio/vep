package vep.app.common.blog

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait BlogIntegrationModule {
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext

  lazy val blogServices = new BlogService(
    configuration
  )
  lazy val blogVerifications = new BlogVerifications(
    commonVerifications,
    blogServices
  )
  lazy val blogRouter = new BlogRouter(
    blogVerifications,
    blogServices,
    userService,
    executionContext
  )
}
