package vep.app.production.company

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import vep.Configuration
import vep.app.common.CommonVerifications
import vep.app.production.company.show.ShowIntegrationModule
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait CompanyIntegrationModule
  extends ShowIntegrationModule {
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
  lazy val companyRoute: Route = companyRouter.route ~ showRouter.route
}
