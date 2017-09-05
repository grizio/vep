package vep.app.production.company.show

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.common.verifications.CommonVerifications
import vep.app.production.company.CompanyService
import vep.app.production.company.show.play.{PlayIntegrationModule, PlayService}
import vep.app.user.UserService

import scala.concurrent.ExecutionContext

trait ShowIntegrationModule
  extends PlayIntegrationModule {
  def userService: UserService
  def commonVerifications: CommonVerifications
  def executionContext: ExecutionContext
  def companyService: CompanyService
  def playService: PlayService
  def configuration: Configuration

  lazy val showVerifications = new ShowVerifications(
    commonVerifications
  )
  lazy val showService = new ShowService(
    playService
  )
  lazy val showRouter = new ShowRouter(
    showVerifications,
    showService,
    companyService,
    configuration,
    userService,
    executionContext
  )
  lazy val showRoute: Route = showRouter.route ~ playRouter.route
}
