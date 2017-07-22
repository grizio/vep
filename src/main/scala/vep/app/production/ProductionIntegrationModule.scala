package vep.app.production

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import vep.Configuration
import vep.app.production.company.CompanyIntegrationModule
import vep.app.production.theater.TheaterIntegrationModule

trait ProductionIntegrationModule
  extends TheaterIntegrationModule
    with CompanyIntegrationModule {
  def configuration: Configuration

  lazy val productionRoute: Route = theaterRouter.route ~ companyRouter.route
}