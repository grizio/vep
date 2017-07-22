package vep.app.production.company

import vep.Configuration
import vep.app.common.CommonVerifications
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait CompanyIntegrationModule {
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext

  lazy val companyVerifications = new CompanyVerifications(
    commonVerifications
  )
  lazy val companyService = new CompanyService(
    configuration
  )
  lazy val companyRouter = new CompanyRouter(
    companyVerifications,
    companyService,
    userService,
    executionContext
  )
}
