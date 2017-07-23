package vep.app.production.company.show

import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.production.company.CompanyService
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait ShowIntegrationModule {
  def configuration: Configuration
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext
  def companyService: CompanyService

  lazy val showVerifications = new ShowVerifications(
    commonVerifications
  )
  lazy val showService = new ShowService(
    configuration
  )
  lazy val showRouter = new ShowRouter(
    showVerifications,
    showService,
    companyService,
    userService,
    executionContext
  )
}
