package vep.app.production

import akka.http.scaladsl.server.Route
import vep.Configuration
import vep.app.production.theater.TheaterIntegrationModule

trait ProductionIntegrationModule
  extends TheaterIntegrationModule {
  def configuration: Configuration

  lazy val productionRoute: Route = theaterRouter.route
}